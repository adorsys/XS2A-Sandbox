package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.ConsentAuthorizeResponse;
import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.rest.api.domain.PIISConsentCreateResponse;
import de.adorsys.ledgers.oba.rest.api.exception.ConsentAuthorizeException;
import io.swagger.annotations.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = AISApi.BASE_PATH, tags = "PSU AIS", description = "Provides access to online banking AIS functionality")
public interface AISApi {
    String BASE_PATH = "/ais";

    @GetMapping(path = "/auth", params = {"redirectId", "encryptedConsentId"})
    @ApiOperation(value = "Entry point for authenticating ais consent requests.")
    ResponseEntity<AuthorizeResponse> aisAuth(
        @RequestParam(name = "redirectId") String redirectId,
        @RequestParam(name = "encryptedConsentId") String encryptedConsentId,
        @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String token);

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
     * @param consentCookieString the cosent cookie
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/login")
    @ApiOperation(value = "Identifies the user by login an pin. Return sca methods information")
    ResponseEntity<ConsentAuthorizeResponse> login(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestParam(value = "login", required = false) String login,
        @RequestParam(value = "pin", required = false) String pin,
        @RequestHeader(name = "Cookie", required = false) String consentCookieString);

    /**
     * Start the consent process. By sending the customer request consent to the core banking.
     *
     * @param encryptedConsentId                the encrypted consent id
     * @param authorisationId                   the authorization id
     * @param consentAndaccessTokenCookieString the consent cookie
     * @param aisConsent                        the consent request object
     * @return ConsentAuthorizeResponse
     */
    @PostMapping("/{encryptedConsentId}/authorisation/{authorisationId}/start")
    @ApiOperation(value = "Starts the cosent authaurization process after user selects which account to grant access to",
        authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> startConsentAuth(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString,
        @RequestBody AisConsentTO aisConsent);


    /**
     * Selects the SCA Method for use.
     *
     * @param encryptedConsentId                the sca id
     * @param authorisationId                   the auth id
     * @param scaMethodId                       sca
     * @param consentAndaccessTokenCookieString the cosent cookie
     * @return ConsentAuthorizeResponse
     */
    @PostMapping("/{encryptedConsentId}/authorisation/{authorisationId}/methods/{scaMethodId}")
    @ApiOperation(value = "Selects the SCA Method for use.", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> selectMethod(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @PathVariable("scaMethodId") String scaMethodId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString);

    /**
     * Provides a TAN for the validation of an authorization
     *
     * @param encryptedConsentId                the sca id
     * @param authorisationId                   the auth id
     * @param consentAndaccessTokenCookieString the cosent cookie
     * @param authCode                          the auth code
     * @return ConsentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/authCode", params = {"authCode"})
    @ApiOperation(value = "Provides a TAN for the validation of an authorization", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<ConsentAuthorizeResponse> authrizedConsent(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString,
        @RequestParam("authCode") String authCode);

    @PostMapping(path = "/piis")
    @ApiOperation(value = "Grant a piis consent", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PIISConsentCreateResponse> grantPiisConsent(
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString, @RequestBody CreatePiisConsentRequestTO piisConsentTO);

    /**
     * Return the list of accounts linked with the current customer.
     *
     * @return : the list of accounts linked with the current customer.
     */
    @GetMapping(path = "/accounts")
    @ApiOperation(value = "List fo Accessible Accounts", authorizations = @Authorization(value = "apiKey"),
        notes = "Returns the list of all accounts linked to the connected user. "
                    + "Call only available to role CUSTOMER.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, response = AccountDetailsTO[].class, message = "List of accounts accessible to the user.")
    })
    ResponseEntity<List<AccountDetailsTO>> getListOfAccounts(@RequestHeader(name = "Cookie", required = false) String accessTokenCookieString);


    /**
     * This call provides the server with the opportunity to close this session and
     * redirect the PSU to the TPP or close the application window.
     * <p>
     * In any case, the session of the user will be closed and cookies will be deleted.
     *
     * @param encryptedConsentId
     * @param authorisationId
     * @return
     */
    @GetMapping(path = "/{encryptedConsentId}/authorisation/{authorisationId}/done", params = {"forgetConsent", "backToTpp"})
    @ApiOperation(value = "Close consent session", authorizations = @Authorization(value = "apiKey"),
        notes = "This call provides the server with the opportunity to close this session and "
                    + "redirect the PSU to the TPP or close the application window.")
    ResponseEntity<ConsentAuthorizeResponse> aisDone(
        @PathVariable("encryptedConsentId") String encryptedConsentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestHeader(name = "Cookie", required = false) String consentAndaccessTokenCookieString,
        @RequestParam(name = "forgetConsent", required = false) Boolean forgetConsent,
        @RequestParam(name = "backToTpp", required = false) Boolean backToTpp) throws ConsentAuthorizeException;

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
                                                           @PathVariable("authorisationId") String authorisationId,
                                                           @RequestHeader(name = "Cookie", required = false) String cookieString);

}

