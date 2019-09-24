package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.exception.PaymentAuthorizeException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
			@RequestHeader(name="Cookie", required=false) String consentCookieString) throws PaymentAuthorizeException;

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
	ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString);

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
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString);

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
	ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
			@PathVariable("encryptedPaymentId") String encryptedPaymentId,
			@PathVariable("authorisationId") String authorisationId,
			@RequestHeader(name="Cookie", required=false) String consentAndaccessTokenCookieString,
			@RequestParam("authCode") String authCode);

    /**
     * Fails PIS authorisation object by its ID.
     *
     * @param encryptedPaymentId ID of Payment
     * @return <code>true</code> if payment authorisation was found and failed. <code>false</code> otherwise.
     */
    @DeleteMapping(path = "/{encryptedPaymentId}/{authorisationId}")
    @ApiOperation(value = "Fail payment authorisation", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
                    + "revoke consent.")
    ResponseEntity<PaymentAuthorizeResponse> failPaymentAuthorisation(@PathVariable("encryptedPaymentId") String encryptedPaymentId,
                                                           @PathVariable("authorisationId") String authorisationId,
                                                           @RequestHeader(name = "Cookie", required = false) String cookieString);

    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed and cookies will be deleted.
     *
     * @param encryptedPaymentId ID of Payment
     * @param authorisationId ID of related Payment Authorisation
     * @return redirect location header with TPP url
     */
    @GetMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/done", params = {"forgetConsent", "backToTpp"})
    @ApiOperation(value = "Close consent session", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
                    + "redirect the PSU to the TPP or close the application window.")
    ResponseEntity<PaymentAuthorizeResponse> pisDone(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndAccessTokenCookieString,
        @RequestParam(name = "forgetConsent", required = false) Boolean forgetConsent,
        @RequestParam(name = "backToTpp", required = false) Boolean backToTpp) throws PaymentAuthorizeException;

}
