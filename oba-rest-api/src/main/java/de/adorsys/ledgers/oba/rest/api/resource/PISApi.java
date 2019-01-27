package de.adorsys.ledgers.oba.rest.api.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@Api(value = PISApi.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public interface PISApi {
	String BASE_PATH = "/pis";

	/**
	 * STEP-P0: payment Entry Point
	 * 
	 * Receptions a payment authorization link. Generate an eca-id associated with the login process.
	 * 
	 * @param redirectId  the redirect is
	 * @param encryptedPaymentId the enc payment idf
	 * @return AuthorizeResponse
	 */
	@GetMapping(path="/auth", params= {"redirectId","encryptedPaymentId"})
	@ApiOperation(value = "Entry point for authenticating payment requests.")
	ResponseEntity<AuthorizeResponse> pisAuth(
			@RequestParam(name = "redirectId") String redirectId,
			@RequestParam(name = "encryptedPaymentId") String encryptedPaymentId);
	
	/**
	 * Identifies the user by login an pin. Return sca methods information
	 * 
	 * @param encryptedPaymentId the encryptedPaymentId
	 * @param authorisationId the auth id
	 * @param login the login 
	 * @param pin the password
	 * @param consentCookieString the cosent cookie
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedPaymentId}/authorisation/{authorisationId}/login")
	@ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
	ResponseEntity<PaymentAuthorizeResponse> login(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestParam("login") String login,
			@RequestParam("pin") String pin, 
			@RequestHeader("Cookie") String consentCookieString);
	
	/**
	 * Calls the consent validation page.
	 * @param scaId the sca id
	 * @param authorisationId the auth id
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedPaymentId}/authorisation/{authorisationId}/initiate")
	@ApiOperation(value = "Calls the consent validation page.", 
		authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString);

	/**
	 * Selects the SCA Method for use.
	 * 
	 * @param scaId the sca id
	 * @param authorisationId the auth id
	 * @param scaMethodId sca
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping("/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
	@ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PaymentAuthorizeResponse> selectMethod(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@PathVariable("scaMethodId") String scaMethodId,
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString);

	/**
	 * Provides a TAN for the validation of an authorization
	 * 
	 * @param scaId the sca id
	 * @param authorisationId the auth id
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @param authCode the auth code
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedPaymentId}/authorisation/{authorisationId}/authCode", params= {"authCode"})
	@ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestHeader("Cookie") String consentAndaccessTokenCookieString,
			@RequestParam("authCode") String authCode);
}
