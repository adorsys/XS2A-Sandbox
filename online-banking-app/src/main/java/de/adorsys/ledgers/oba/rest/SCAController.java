package de.adorsys.ledgers.oba.rest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.ledgers.LedgersRestClient;
import de.adorsys.ledgers.domain.SCAValidationRequest;
import de.adorsys.ledgers.domain.sca.AuthCodeDataTO;
import de.adorsys.ledgers.domain.sca.SCAGenerationResponse;
import de.adorsys.ledgers.domain.sca.SCAMethodTO;
import de.adorsys.ledgers.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.auth.BaererAuthHeader;
import de.adorsys.ledgers.oba.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController(SCAController.BASE_PATH)
@RequestMapping(SCAController.BASE_PATH)
@Api(value = SCAController.BASE_PATH, tags = "PSU SCA", description = "Provides access to one time password for strong customer authentication.")
public class SCAController {
	public static final String SCA_ID_REQUEST_PARAM = "scaId";
	public static final String LOGIN_SCA_ID = "/login/{scaId}";
	public static final String BASE_PATH = "/sca";

	@Autowired
	private LedgersRestClient ledger;
	
	@Autowired
	private ConsentReferencePolicy referencePolicy;
	
	@Autowired
	private ResponseUtils responseUtils;

	/**
	 * Validates the login and password of a user. This request is associated with
	 * an scaId that is directly bound to the consentId/paymentId used in the xs2a 
	 * redirect request. BTW the scaId can be the initiating consent id itself or
	 * a random id mapping to the consentId (resp. paymentId)
	 * 
	 * Implementation first validates existence of the consent. If the consent does
	 * not exist or has the wrong status, the request is rejected.
	 * 
	 * Call the backend middleware to obtain a login token. This is a token only
	 * valid for the sca process.
	 * 
	 * Store the login token in a cookie.
	 * 
	 * If the user has no sca method, the return de consent access token.
	 * 
	 * If the user has only one sca method, sent authentication code to the user and
	 * return the sac method id in the AuthorizeResponse
	 * 
	 * If the user has more than one sca methods, returns the list of sca methods in
	 * the AuthorizeResponse and wait for sca method selection.
	 * 
	 * Method expects
	 * 
	 * 
	 * @param scaId the id of the authaurization
	 * @param login the customer banking login
	 * @param pin the customer banking pin
	 * @param consentCookieString the cookie string
	 * @param response the authorization response object
	 * @return the auth response
	 */
	@PostMapping(LOGIN_SCA_ID)
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	public ResponseEntity<AuthorizeResponse> login(
			@PathVariable(name = SCA_ID_REQUEST_PARAM) String scaId,
			@RequestParam("login") String login,
			@RequestParam("pin") String pin, 
			@CookieValue(name="CONSENT") String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
			authResponse.setScaId(scaId);
		} catch (InvalidConsentException e) {
			return responseUtils.respond(authResponse, "Unknown credentials");
		}

		// Authorize
		BearerTokenTO loginToken = ledger.authorise(login, pin, "CUSTOMER");
		if(loginToken==null) {// interupt
			return responseUtils.respond(authResponse, "Unknown credentials");
		}
		
		// Read sca methods.
		List<SCAMethodTO> userScaMethods = ledger.getUserScaMethods(
				BaererAuthHeader.value(loginToken.getAccess_token()), login);
		
		if(userScaMethods.isEmpty()) {// no sca
			authResponse.setUserMessage("User has no sca methods configured. Login not possible.");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponse);
		} else if(userScaMethods.size()==1) {
			String userScaMethodId = userScaMethods.iterator().next().getId();
			genLoginCode(login, authResponse, loginToken, userScaMethodId);
			// Store result in token.
		} else {
			authResponse.setScaMethods(userScaMethods);
		}
		
		responseUtils.setCookies(response, consentReference, loginToken.getAccess_token(), loginToken.getAccessTokenObject());
		return ResponseEntity.ok(authResponse);
	}

	/**
	 * Select a method for sending the authentication code.
	 * 
	 * 
	 * @param scaId the id of the authaurization
	 * @param methodId the auth method id
	 * @param auth the authentication object
	 * @param consentCookieString the cookie string
	 * @param response the authorization response object
	 * @return the auth response.
	 */
	@PostMapping("/{scaId}/methods/{methodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<AuthorizeResponse> selectMethod(
			@PathVariable(name = SCA_ID_REQUEST_PARAM) String scaId,
			@PathVariable(name = "methodId") String methodId,
			MiddlewareAuthentication auth,
			@CookieValue(name="CONSENT") String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setScaId(scaId);
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse);
		}

		BearerTokenTO loginToken = auth.getBearerToken();
		
		genLoginCode(loginToken.getAccessTokenObject().getActor(), authResponse, loginToken, methodId);
		
		responseUtils.setCookies(response, consentReference, loginToken.getAccess_token(), loginToken.getAccessTokenObject());
		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/{scaId}/authCode/{scaOpId}")
	@ApiOperation(value = "Validate the provided authentication code.", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<AuthorizeResponse> validate(
			@PathVariable(name = SCA_ID_REQUEST_PARAM) String scaId,
			@PathVariable(name = "scaOpId") String scaOpId,
			@RequestParam(name="authCode") String authCode,
			MiddlewareAuthentication auth,
			@CookieValue(name="CONSENT") String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setScaId(scaId);
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse);
		}

		BearerTokenTO loginToken = auth.getBearerToken();
		
		String opData = loginToken.getAccessTokenObject().getJti();
		SCAValidationRequest request = new SCAValidationRequest(opData, authCode);
		BearerTokenTO bankingToken = ledger.validate(BaererAuthHeader.value(loginToken.getAccess_token()), scaOpId, request);
		responseUtils.setCookies(response, consentReference, bankingToken.getAccess_token(), bankingToken.getAccessTokenObject());
		return ResponseEntity.ok(authResponse);
	}

	private void genLoginCode(String login, final AuthorizeResponse authResponse, BearerTokenTO loginToken,
			String scaMethodId) {
		String opData = loginToken.getAccessTokenObject().getJti();
		String tanBlock = "The TAN is %s";
		Date iat = loginToken.getAccessTokenObject().getIat();
		LocalDateTime ldt = LocalDateTime.ofInstant(iat.toInstant(), ZoneId.systemDefault());
		String loginTime = ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		int validitySeconds = 180;
		String userMessage = String.format("Login into your account with login name: %s at %s. Tan valid for %s seconds. "
				, login, loginTime, validitySeconds + tanBlock);
		AuthCodeDataTO authCodeDataTO = new AuthCodeDataTO(login, scaMethodId, null, opData, userMessage, validitySeconds);
		SCAGenerationResponse generationResponse = ledger.generate(BaererAuthHeader.value(loginToken.getAccess_token()), authCodeDataTO);
		authResponse.setScaOpId(generationResponse.getOpId());
	}
}
