package de.adorsys.ledgers.oba.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Api(value = SCAApi.BASE_PATH, tags = "PSU SCA", description = "Provides access to one time password for strong customer authentication.")
public interface SCAApi {
	String BASE_PATH = "/sca";

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
	@PostMapping("/login")
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	ResponseEntity<AuthorizeResponse> login(
			@RequestParam("login") String login,
			@RequestParam("pin") String pin);

	/**
	 * Select a method for sending the authentication code.
	 * 
	 * @param scaId the id of the login process
	 * @param methodId the auth method id
	 * @param authorisationId the auth id.
	 * @param cookies the cookie string
	 * @return the auth response.
	 */
	@PostMapping(path="/{scaId}/authorisation/{authorisationId}/methods/{methodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<AuthorizeResponse> selectMethod(
			@PathVariable("scaId") String scaId,
			@PathVariable("authorisationId") String authorisationId,
			@PathVariable("methodId") String methodId,
			@RequestHeader("Cookie") String cookies);

	@PostMapping(path="/{scaId}/authorisation/{authorisationId}/authCode", params="authCode")
	@ApiOperation(value = "Validate the provided authentication code.", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<AuthorizeResponse> validateAuthCode(
			@PathVariable("scaId") String scaId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestParam(name="authCode") String authCode,
			@RequestHeader("Cookie") String cookies);
}
