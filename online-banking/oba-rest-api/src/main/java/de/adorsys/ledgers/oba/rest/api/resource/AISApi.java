/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Tag(name = "PSU AIS. Provides access to online banking AIS functionality")
public interface AISApi {
    String BASE_PATH = "/ais";


    /**
     * Identifies the user by login and pin.
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
    @Operation(summary = "Identifies the user by login an pin. Return sca methods information")
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
    @Operation(summary = "Starts the cosent authaurization process after user selects which account to grant access to")
    @SecurityRequirement(name = "apiKey")
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
    @Operation(summary = "Selects the SCA Method for use.")
    @SecurityRequirement(name = "apiKey")
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
    @Operation(summary = "Provides a TAN for the validation of an authorization")
    @SecurityRequirement(name = "apiKey")
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
    @Operation(summary = "Close consent session", description = "This call provides the server with the opportunity to close this session and "
                    + "redirect the PSU to the TPP or close the application window.")
    @SecurityRequirement(name = "apiKey")
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
    @Operation(summary = "Revoke consent", description = "This call provides the server with the opportunity to close this session and "
            + "revoke consent.")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<ConsentAuthorizeResponse> revokeConsent(@PathVariable("encryptedConsentId") String encryptedConsentId,
                                                           @PathVariable("authorisationId") String authorisationId);
}

