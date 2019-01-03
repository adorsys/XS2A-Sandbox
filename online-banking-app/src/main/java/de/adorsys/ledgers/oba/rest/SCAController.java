package de.adorsys.ledgers.oba.rest;

import java.util.Arrays;

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

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.consentref.ConsentReferencePolicy;
import de.adorsys.ledgers.oba.consentref.InvalidConsentException;
import de.adorsys.ledgers.oba.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.domain.OnlineBankingResponse;
import de.adorsys.ledgers.oba.domain.PsuMessage;
import de.adorsys.ledgers.oba.domain.PsuMessageCategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController(SCAController.BASE_PATH)
@RequestMapping(SCAController.BASE_PATH)
@Api(value = SCAController.BASE_PATH, tags = "PSU SCA", description = "Provides access to one time password for strong customer authentication.")
public class SCAController {
	public static final String BASE_PATH = "/sca";

	@Autowired
	private UserMgmtRestClient ledgersUserMgmt;

	@Autowired
	private ConsentReferencePolicy referencePolicy;
	
	@Autowired
	private ResponseUtils responseUtils;
	
	@Autowired
	private AuthRequestInterceptor authInterceptor;

	/**
	 * STEP-P1, STEP-A1: Validates the login and password of a user. This request is associated with
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
	@PostMapping("/{scaId}/login")
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	public ResponseEntity<OnlineBankingResponse> login(
			@PathVariable("scaId") String scaId,
			@RequestParam("login") String login,
			@RequestParam("pin") String pin, 
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
			authResponse.setScaId(scaId);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse, response);
		}

		// Authorize
		SCALoginResponseTO loginResponse = ledgersUserMgmt.authorise(login, pin, UserRoleTO.CUSTOMER).getBody();
		
		ScaStatusTO scaStatus = prepareAuthResponse(authResponse, loginResponse);
		BearerTokenTO bearerToken = loginResponse.getBearerToken();
		switch (scaStatus) {
		case PSUAUTHENTICATED:
			// Passwort check was successfull.
			// sca method must be selected.
			// PSU Message will contain message for selection of sca method.
			authResponse.setScaMethods(loginResponse.getScaMethods());
		case FINALISED:
			// SCA was successfull
		case EXEMPTED:
			// Passwort check was successfull.
			// No auth code required.
			// PSU Message for login complete.
		case SCAMETHODSELECTED:
			// Passwort check was successfull.
			// Method was auto selected ans auth code was sent.
			// PSUMessage will contain prompt for auth code.
			responseUtils.setCookies(response, consentReference, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		case PSUIDENTIFIED:
			// We know the psu but the password check didn't work.
			// Recolect password
			responseUtils.setCookies(response, consentReference, null, null);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(authResponse);
		case STARTED:
		case FAILED:
		default:
			// failled Message. No repeat. Delete cookies.
			responseUtils.removeCookies(response);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	private ScaStatusTO prepareAuthResponse(AuthorizeResponse authResponse, SCALoginResponseTO loginResponse) {
		// Process response.
		ScaStatusTO scaStatus = loginResponse.getScaStatus();
		authResponse.setScaStatus(scaStatus);
		authResponse.setAuthorisationId(loginResponse.getAuthorisationId());
		PsuMessage psuMessage = new PsuMessage().category(PsuMessageCategory.INFO).text(loginResponse.getPsuMessage());
		authResponse.setPsuMessages(Arrays.asList(psuMessage));
		return scaStatus;
	}

	/**
	 * Select a method for sending the authentication code.
	 * 
	 * @param scaId the id of the authaurization
	 * @param methodId the auth method id
	 * @param authorisationId the auth id.
	 * @param auth the authentication object
	 * @param consentCookieString the cookie string
	 * @param response the authorization response object
	 * @return the auth response.
	 */
	@PostMapping("/{scaId}/authorisation/{authorisationId}/methods/{methodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<OnlineBankingResponse> selectMethod(
			@PathVariable("scaId") String scaId,
			@PathVariable("authorisationId") String authorisationId,
			@PathVariable(name = "methodId") String methodId,
			MiddlewareAuthentication auth,
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setScaId(scaId);
		authResponse.setAuthorisationId(authorisationId);
		
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse, response);
		}
		
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			SCALoginResponseTO loginResponse = ledgersUserMgmt.selectMethod(scaId, authorisationId, methodId).getBody();
			prepareAuthResponse(authResponse, loginResponse);
			BearerTokenTO bearerToken = loginResponse.getBearerToken();
			responseUtils.setCookies(response, consentReference, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}

	@PostMapping(name="/{scaId}/authorisation/{authorisationId}/authCode", params="authCode")
	@ApiOperation(value = "Validate the provided authentication code.", authorizations = @Authorization(value = "apiKey"))
	public ResponseEntity<OnlineBankingResponse> validate(
			@PathVariable("scaId") String scaId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestParam(name="authCode") String authCode,
			MiddlewareAuthentication auth,
			@CookieValue(name=ResponseUtils.CONSENT_COOKIE_NAME) String consentCookieString,
			HttpServletResponse response) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setScaId(scaId);
		ConsentReference consentReference;
		try {
			consentReference = referencePolicy.fromRequest(scaId, consentCookieString);
		} catch (InvalidConsentException e) {
			return responseUtils.unknownCredentials(authResponse, response);
		}

		
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			
			SCALoginResponseTO loginResponse = ledgersUserMgmt.authorizeLogin(scaId, authorisationId, authCode).getBody();
			prepareAuthResponse(authResponse, loginResponse);
			BearerTokenTO bearerToken = loginResponse.getBearerToken();
			responseUtils.setCookies(response, consentReference, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}
}
