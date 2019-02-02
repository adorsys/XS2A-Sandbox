package de.adorsys.ledgers.oba.rest.server.resource;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessageCategory;
import de.adorsys.ledgers.oba.rest.api.resource.SCAApi;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController(SCAController.BASE_PATH)
@RequestMapping(SCAController.BASE_PATH)
@Api(value = SCAController.BASE_PATH, tags = "PSU SCA", description = "Provides access to one time password for strong customer authentication.")
public class SCAController implements SCAApi {
	public static final String BASE_PATH = "/sca";

	@Autowired
	private UserMgmtRestClient ledgersUserMgmt;
	
	@Autowired
	private ResponseUtils responseUtils;
	
	@Autowired
	private AuthRequestInterceptor authInterceptor;
	
	@Autowired
	private HttpServletResponse response;
	@Autowired
	private MiddlewareAuthentication auth;
	

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
	 * If the user has no sca method, then return the consent access token.
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
	 * @param login the customer banking login
	 * @param pin the customer banking pin
	 * @return the auth response
	 */
	@Override
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	public ResponseEntity<AuthorizeResponse> login(String login, String pin) {
		// Authorize
		SCALoginResponseTO loginResponse = ledgersUserMgmt.authorise(login, pin, UserRoleTO.CUSTOMER).getBody();
		
		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		ScaStatusTO scaStatus = prepareAuthResponse(authResponse, loginResponse);
		BearerTokenTO bearerToken = loginResponse.getBearerToken();
		switch (scaStatus) {
		case PSUIDENTIFIED:
			// Password check was successful.
			// sca method must be selected.
			// PSU Message will contain message for selection of sca method.
			authResponse.setScaMethods(loginResponse.getScaMethods());
		case FINALISED:
			// SCA was successfull
		case EXEMPTED:
		case PSUAUTHENTICATED:
			// Password check was successful.
			// No auth code required.
			// PSU Message for login complete.
		case SCAMETHODSELECTED:
			// Passwor check was successful.
			// Method was auto selected ans auth code was sent.
			// PSUMessage will contain prompt for auth code.
			responseUtils.setCookies(response, null, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		case STARTED:
		case FAILED:
		default:
			// failed Message. No repeat. Delete cookies.
			responseUtils.removeCookies(response);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	private ScaStatusTO prepareAuthResponse(AuthorizeResponse authResponse, SCALoginResponseTO loginResponse) {
		// Process response.
		ScaStatusTO scaStatus = loginResponse.getScaStatus();
		authResponse.setScaStatus(scaStatus);
		authResponse.setAuthorisationId(loginResponse.getAuthorisationId());
		authResponse.setEncryptedConsentId(loginResponse.getScaId());
		PsuMessage psuMessage = new PsuMessage().category(PsuMessageCategory.INFO).text(loginResponse.getPsuMessage());
		authResponse.setPsuMessages(Arrays.asList(psuMessage));
		return scaStatus;
	}

	/**
	 * Select a method for sending the authentication code.
	 * 
	 * @param scaId the id of the login process
	 * @param methodId the auth method id
	 * @param authorisationId the auth id.
	 * @param cookies the cookie string
	 * @return the auth response.
	 */
	@Override
	public ResponseEntity<AuthorizeResponse> selectMethod(String scaId, String authorisationId, String methodId, String cookies) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setEncryptedConsentId(scaId);
		authResponse.setAuthorisationId(authorisationId);
		
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			SCALoginResponseTO loginResponse = ledgersUserMgmt.selectMethod(scaId, authorisationId, methodId).getBody();
			prepareAuthResponse(authResponse, loginResponse);
			BearerTokenTO bearerToken = loginResponse.getBearerToken();
			responseUtils.setCookies(response, null, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}

	@Override
	public ResponseEntity<AuthorizeResponse> validateAuthCode(String scaId, String authorisationId, String authCode,String cookies) {

		// build response
		AuthorizeResponse authResponse = new AuthorizeResponse();
		authResponse.setEncryptedConsentId(scaId);
		authResponse.setAuthorisationId(authorisationId);
		
		try {
			authInterceptor.setAccessToken(auth.getBearerToken().getAccess_token());
			
			SCALoginResponseTO loginResponse = ledgersUserMgmt.authorizeLogin(scaId, authorisationId, authCode).getBody();
			prepareAuthResponse(authResponse, loginResponse);
			BearerTokenTO bearerToken = loginResponse.getBearerToken();
			responseUtils.setCookies(response, null, bearerToken.getAccess_token(), bearerToken.getAccessTokenObject());
			return ResponseEntity.ok(authResponse);
		} finally {
			authInterceptor.setAccessToken(null);
		}
	}
}
