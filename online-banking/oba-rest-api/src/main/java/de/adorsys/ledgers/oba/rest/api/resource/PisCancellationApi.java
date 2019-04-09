package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = PisCancellationApi.BASE_PATH, tags = "PSU PIS", description = "Provides access to online banking payment functionality")
public interface PisCancellationApi {
	String BASE_PATH = "/pis-cancellation";


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
        @RequestHeader(name = "Cookie", required = false) String consentCookieString);

	/**
	 * Calls the consent validation page.
	 * @param encryptedPaymentId the sca id
	 * @param authorisationId the auth id
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedPaymentId}/authorisation/{authorisationId}/initiate")
	@ApiOperation(value = "Calls the consent validation page.",
		authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PaymentAuthorizeResponse> initiatePaymentCancellation(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString);

	/**
	 * Selects the SCA Method for use.
	 *
	 * @param encryptedPaymentId the sca id
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
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString);

	/**
	 * Provides a TAN for the validation of an authorization
	 *
	 * @param encryptedPaymentId the sca id
	 * @param authorisationId the auth id
	 * @param consentAndaccessTokenCookieString the cosent cookie
	 * @param authCode the auth code
	 * @return PaymentAuthorizeResponse
	 */
	@PostMapping(path="/{encryptedPaymentId}/authorisation/{authorisationId}/authCode", params= {"authCode"})
	@ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
	ResponseEntity<PaymentAuthorizeResponse> authorisePayment(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString,
        @RequestParam("authCode") String authCode);
}
