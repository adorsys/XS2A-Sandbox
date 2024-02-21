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

package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReportTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountReport;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.DepositAccount;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "TPP Accounts management")
public interface TppAccountsRestApi {

    String BASE_PATH = "/tpp/accounts";

    @Operation(summary = "Create account for a given user", description = "Endpoint to a deposit account for a user with given ID")
    @SecurityRequirement(name = "apiKey")
    @PostMapping
    ResponseEntity<Boolean> createAccount(@RequestParam(value = "userId") String userId, @RequestBody DepositAccount account);

    @Operation(summary = "Update Account access for a given user", description = "Endpoint to update account access with given iban for a user with given ID ")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/access")
    ResponseEntity<Void> updateAccountAccess(@RequestBody AccountAccess accountAccess);

    /**
     * Returns the list of accounts that belong to the same branch as STAFF user.
     *
     * @return list of accounts that belongs to the same branch as staff user.
     */
    @Operation(summary = "Get list of Accessible Accounts", description = "Returns the list of all accounts connected to the given TPP")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDetailsTO[].class)),
            description = "List of accounts accessible to the TPP.")
    })
    @GetMapping
    ResponseEntity<List<AccountDetailsTO>> getAllAccounts();

    /**
     * Returns the list of accounts that belong to the same branch as STAFF user, paged view.
     *
     * @return list of accounts that belongs to the same branch as staff user.
     */
    @Operation(summary = "Get list of Accessible Accounts, paged view", description = "Returns the list of all accounts connected to the given TPP")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDetailsTO[].class)),
            description = "List of accounts accessible to the TPP.")
    })
    @GetMapping("/page")
    ResponseEntity<CustomPageImpl<AccountDetailsTO>> getAllAccounts(
        @RequestParam(value = "queryParam", required = false, defaultValue = "") String queryParam,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "25") int size,
        @RequestParam(value = "withBalance", required = false, defaultValue = "false") boolean withBalance);

    /**
     * Returns a single account by its ID if it belongs to the same branch as STAFF user.
     *
     * @return single account by its ID if it belongs to the same branch as STAFF user.
     */
    @Operation(summary = "Get an account by its ID", description = "Returns the account by its ID if it belongs to the TPP")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDetailsTO[].class)),
            description = "Account details by its ID if it is accessible by the TPP")
    })
    @GetMapping(value = "/{accountId}")
    ResponseEntity<AccountDetailsTO> getSingleAccount(@PathVariable("accountId") String accountId);

    /**
     * Returns an account report by its ID if it belongs to the same branch as STAFF user.
     *
     * @return single account report by its ID if it belongs to the same branch as STAFF user.
     */
    @Operation(summary = "Get an account report by its ID", description = "Returns the account report by its ID if it belongs to the TPP")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountReportTO.class)),
            description = "Account report by its ID if it is accessible by the TPP")
    })
    @GetMapping(value = "/report/{accountId}")
    ResponseEntity<AccountReport> accountReport(@PathVariable("accountId") String accountId);

    /**
     * Returns a single account by its ID if it belongs to the same branch as STAFF user.
     *
     * @return single account by its ID if it belongs to the same branch as STAFF user.
     */
    @Operation(summary = "Deposit cash to an account by its ID", description = "Deposits cash to the account by its ID if it belongs to the TPP")
    @SecurityRequirement(name = "apiKey")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200",
            content = @Content(schema = @Schema(implementation = AccountDetailsTO[].class)),
            description = "Deposits cash to the account by its ID")
    })
    @PostMapping(value = "/{accountId}/deposit-cash")
    ResponseEntity<Void> depositCash(@PathVariable("accountId") String accountId, @RequestBody AmountTO amount);

    @GetMapping("/example")
    @Operation(summary = "Download account template")
    ResponseEntity<Resource> downloadAccountTemplate();

    @Operation(summary = "Block/Unblock Account", description = "Changes block state for given account, returns status being set to the block")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/status")
    ResponseEntity<Boolean> changeStatus(@RequestParam(value = "accountId") String accountId);

    @Operation(summary = "Update credit limit for account",description = "Enables/Disables credit limit for certain account")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/credit")
    ResponseEntity<Void> updateCreditLimit(@RequestParam(value = "accountId") String accountId, @RequestBody BigDecimal creditAmount);
}
