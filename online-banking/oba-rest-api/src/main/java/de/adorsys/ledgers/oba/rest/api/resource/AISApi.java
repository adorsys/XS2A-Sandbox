package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Api(value = AISApi.BASE_PATH, tags = "PSU AIS. Provides access to online banking AIS functionality")
public interface AISApi {
    String BASE_PATH = "/ais";


    /**
     * Identifies the user by login an pin.
     * <p>
     * The returned object contains:
     * <ul>
     * <li>A list of accounts
     * <p>This is supposed to be used to display the list of accounts to the psu</p>
     * </li>
     * <li>An AisConsent object
     * <p>This consent is initialized, but might not contain any more information</p>
     * </li>
     * </ul>
     *
     * @param encryptedConsentId  the encryptedConsentId
     * @param authorisationId     the auth id
     * @param login               the login
     * @param pin                 the password
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/login")
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    ResponseEntity<ConsentAuthorizeResponse> login(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "login", required = false) String login,
        @RequestParam(value = "pin", required = false) String pin);

    /**
     * Start the consent process. By sending the customer request consent to the core banking.
     *
     * @param encryptedConsentId                the encrypted consent id
     * @param authorisationId                   the authorization id
     * @param aisConsent                        the consent request object
     * @return ConsentAuthorizeResponse
     */
    @PostMapping("/{encryptedConsentId}/authorisation/{authorisationId}/start")
    @ApiOperation(value = "Starts the cosent authaurization process after user selects which account to grant access to",
        authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestBody AisConsentTO aisConsent);


    /**
     * Selects the SCA Method for use.
     *
     * @param encryptedConsentId                the sca id
     * @param authorisationId                   the auth id
     * @param scaMethodId                       sca
     * @return ConsentAuthorizeResponse
     */
    @PostMapping("/{encryptedConsentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
    @ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> selectMethod(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @PathVariable("scaMethodId") String scaMethodId);

    /**
     * Provides a TAN for the validation of an authorization
     *
     * @param encryptedConsentId                the sca id
     * @param authorisationId                   the auth id
     * @param authCode                          the auth code
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/authCode", params = {"authCode"})
    @ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam("authCode") String authCode);


    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed.
     *
     * @param encryptedConsentId
     * @param authorisationId
     * @return
     */
    @GetMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/done")
    @ApiOperation(value = "Close consent session", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
                    + "redirect the PSU to the TPP or close the application window.")
    ResponseEntity<ConsentAuthorizeResponse> aisDone(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(name = "oauth2", required = false, defaultValue = "false") boolean isOauth2Integrated,
        @RequestParam(name = "authConfirmationCode", required = false) String authConfirmationCode);

    /**
     * Fails AIS Consent authorisation object by its ID.
     *
     * @param encryptedConsentId ID of Consent
     * @return <code>true</code> if consent authorisation was found and failed. <code>false</code> otherwise.
     */
    @DeleteMapping(path = "/{encryptedConsentId}/{authorisationId}")
    @ApiOperation(value = "Revoke consent", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
            + "revoke consent.")
    ResponseEntity<ConsentAuthorizeResponse> revokeConsent(@PathVariable("encryptedConsentId") String encryptedConsentId,
                                                           @PathVariable("authorisationId") String authorisationId);
}

