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

package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Online Banking Account Information")
public interface ObaAisApi {
    String BASE_PATH = "/api/v1/ais";
    String LOCAL_DATE_YYYY_MM_DD_FORMAT = "yyyy-MM-dd";
    String DATE_TO_QUERY_PARAM = "dateTo";
    String DATE_FROM_QUERY_PARAM = "dateFrom";

    /**
     * @param userLogin login of current user
     * @return List of accounts for user
     */
    @GetMapping(path = "/accounts/{userLogin}")
    @Operation(summary = "Get List of users accounts")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<List<AccountDetailsTO>> accounts(@PathVariable("userLogin") String userLogin);

    /**
     * @param accountId selected account id
     * @return account details for queried account
     */
    @GetMapping(path = "/account/{accountId}")
    @Operation(summary = "Get account details by account id")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<AccountDetailsTO> account(@PathVariable(name = "accountId") String accountId);

    /**
     * @param accountId selected accounts id
     * @param dateFrom  date from which the user requests to see transactions
     * @param dateTo    date until which user requests to see transactions
     * @return List of transactions for account
     */
    @GetMapping(path = "/transactions/{accountId}")
    @Operation(summary = "Get List of transactions for queried account per dates selected")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<List<TransactionTO>> transactions(@PathVariable(name = "accountId") String accountId,
                                                     @RequestParam(name = DATE_FROM_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateFrom,
                                                     @RequestParam(name = DATE_TO_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateTo);

    /**
     * @param accountId selected accounts id
     * @param dateFrom  date from which the user requests to see transactions
     * @param dateTo    date until which user requests to see transactions
     * @return List of transactions for account
     */
    @GetMapping(path = "/transactions/{accountId}/page")
    @Operation(summary = "Get List of transactions for queried account per dates selected, paged view")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<CustomPageImpl<TransactionTO>> transactions(@PathVariable(name = "accountId") String accountId,
                                                               @RequestParam(name = DATE_FROM_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateFrom,
                                                               @RequestParam(name = DATE_TO_QUERY_PARAM, required = false) @DateTimeFormat(pattern = LOCAL_DATE_YYYY_MM_DD_FORMAT) LocalDate dateTo,
                                                               @RequestParam(required = false, defaultValue = "0") int page, @RequestParam(required = false, defaultValue = "25") int size);

    /**
     * @return List of pending periodic payments
     */
    @GetMapping(path = "/payments/pending")
    @Operation(summary = "Load Pending Periodic Payments")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<CustomPageImpl<PaymentTO>> getPendingPeriodicPayments(@RequestParam(required = false, defaultValue = "0") int page,
                                                                         @RequestParam(required = false, defaultValue = "25") int size);


    /**
     * @return List of pending periodic payments
     */
    @GetMapping(path = "/payments")
    @Operation(summary = "Load All Payments")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<CustomPageImpl<PaymentTO>> getAllPayments(@RequestParam(required = false, defaultValue = "0") int page,
                                                             @RequestParam(required = false, defaultValue = "25") int size);
}
