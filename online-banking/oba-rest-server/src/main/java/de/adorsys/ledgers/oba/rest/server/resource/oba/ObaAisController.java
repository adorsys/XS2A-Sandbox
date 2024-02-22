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

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAisApi;
import de.adorsys.ledgers.oba.service.api.service.AisService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

import static de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAisApi.BASE_PATH;

@Slf4j
@RestController
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
public class ObaAisController implements ObaAisApi {
    private final AisService aisService;
    private final PaymentRestClient paymentRestClient;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<AccountDetailsTO>> accounts(String userLogin) {
        return ResponseEntity.ok(aisService.getAccounts(userLogin));
    }

    @Override
    public ResponseEntity<AccountDetailsTO> account(String accountId) {
        return ResponseEntity.ok(aisService.getAccount(accountId));
    }

    @Override
    public ResponseEntity<List<TransactionTO>> transactions(String accountId, LocalDate dateFrom, LocalDate dateTo) {
        return ResponseEntity.ok(aisService.getTransactions(accountId, dateFrom, dateTo));
    }

    @Override
    public ResponseEntity<CustomPageImpl<TransactionTO>> transactions(String accountId, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        return ResponseEntity.ok(aisService.getTransactions(accountId, dateFrom, dateTo, page, size));
    }

    @Override
    public ResponseEntity<CustomPageImpl<PaymentTO>> getPendingPeriodicPayments(int page, int size) {
        return paymentRestClient.getPendingPeriodicPaymentsPaged(page, size);
    }

    @Override
    public ResponseEntity<CustomPageImpl<PaymentTO>> getAllPayments(int page, int size) {
        return paymentRestClient.getAllPaymentsPaged(page, size);
    }
}
