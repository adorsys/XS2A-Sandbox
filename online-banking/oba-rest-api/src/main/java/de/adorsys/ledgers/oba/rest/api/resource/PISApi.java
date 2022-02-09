/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReferenceTO;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = PISApi.BASE_PATH, tags = "PSU PIS. Provides access to online banking payment functionality")
public interface PISApi {
    String BASE_PATH = "/pis";


    /**
     * Identifies the user by login an pin. Return authorisation information.
     * <p>
     * Note: this method doesn't launch payment initiation directly, for this purpose use the
     * 'initiatePayment' method.
     *
     * @param encryptedPaymentId encrypted payment ID
     * @param authorisationId    authorisation ID
     * @param login              the login
     * @param pin                the password
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
     * Starts payment initiation in ledgers.
     *
     * @param encryptedPaymentId encrypted payment ID
     * @param authorisationId    authorisation ID
     * @param accountReference   selected account reference from the frontend in case of empty debtor account
     * @return PaymentAuthorizeResponse
     */
    @PostMapping(path = "/{encryptedPaymentId}/authorisation/{authorisationId}/initiate")
    @ApiOperation(value = "Calls the consent validation page.",
        authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<PaymentAuthorizeResponse> initiatePayment(
        @PathVariable("encryptedPaymentId") String encryptedPaymentId,
        @PathVariable("authorisationId") String authorisationId,
        @RequestBody AccountReferenceTO accountReference);

    /**
     * Selects the SCA Method for use.
     *
     * @param encryptedPaymentId encrypted payment ID
     * @param authorisationId    authorisation ID
     * @param scaMethodId        ID of chosen SCA method
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
     * @param encryptedPaymentId encrypted payment ID
     * @param authorisationId    authorisation ID
     * @param authCode           the auth code (TAN)
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
     * In any case, the session of the user will be closed.
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

    /**
     * This call allows to get all accounts for given PSU.
     * <p>
     * Token authorisation is required.
     *
     * @return list of bank accounts for given PSU.
     */
    @GetMapping(path = "/accounts")
    @ApiOperation(value = "Read account list for given PSU", authorizations = @Authorization(value = "apiKey"),
        notes = "This call allows to get all accounts for given PSU.")
    ResponseEntity<List<AccountDetailsTO>> getAccountList();

}
