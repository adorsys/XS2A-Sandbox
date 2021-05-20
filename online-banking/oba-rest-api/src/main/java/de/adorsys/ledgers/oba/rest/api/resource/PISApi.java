package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = PISApi.BASE_PATH, tags = "PSU PIS. Provides access to online banking payment functionality")
public interface PISApi {
    String BASE_PATH = "/pis";


    /**
     * Identifies the user by login an pin. Return sca methods information
     *
     * @param encryptedPaymentId  the encryptedPaymentId
     * @param authorisationId     the auth id
     * @param login               the login
     * @param pin                 the password
     * @return PaymentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/login")
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    ResponseEntity<PaymentAuthorizeResponse> login(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "login", required = false) String login,
        @RequestParam(value = "pin", required = false) String pin);

    /**
     * Calls the consent validation page.
     *
     * @param encryptedPaymentId                the sca id
     * @param authorisationId                   the auth id
     * @return PaymentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/initiate")
    @ApiOperation(value = "Calls the consent validation page.",
        authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId);

    /**
     * Selects the SCA Method for use.
     *
     * @param encryptedPaymentId                the sca id
     * @param authorisationId                   the auth id
     * @param scaMethodId                       sca
     * @return PaymentAuthorizeResponse
     */
    @PostMapping("/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
    @ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PaymentAuthorizeResponse> selectMethod(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @PathVariable("scaMethodId") String scaMethodId);

    /**
     * Provides a TAN for the validation of an authorization
     *
     * @param encryptedPaymentId                the sca id
     * @param authorisationId                   the auth id
     * @param authCode                          the auth code
     * @return PaymentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/authCode", params = {"authCode"})
    @ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PaymentAuthorizeResponse> authrizedPayment(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
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
                                                                      @PathVariable("authorisationId") String authorisationId);

    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed .
     *
     * @param encryptedPaymentId ID of Payment
     * @param authorisationId    ID of related Payment Authorisation
     * @return redirect location header with TPP url
     */
    @GetMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/done")
    @ApiOperation(value = "Close consent session", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
                    + "redirect the PSU to the TPP or close the application window.")
    ResponseEntity<PaymentAuthorizeResponse> pisDone(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(name = "oauth2", required = false, defaultValue = "false") boolean isOauth2Integrated,
        @RequestParam(name = "authConfirmationCode", required = false) String authConfirmationCode);
}
