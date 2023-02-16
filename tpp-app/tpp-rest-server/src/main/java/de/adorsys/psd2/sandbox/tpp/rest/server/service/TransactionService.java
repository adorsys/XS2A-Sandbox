/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.MockTransactionsStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.MockTransactionDataConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final ParseService parseService;
    private final MockTransactionsStaffRestClient transactionsStaffRestClient;
    private final MockTransactionDataConverter transactionDataConverter;

    public Map<String, String> uploadUserTransaction(MultipartFile multipart) {
        log.info("parsing transaction data");
        List<UserTransaction> transactions = parseService.convertFileToTargetObject(multipart, UserTransaction.class);
        log.info("{} transactions parsed", transactions.size());
        Map<String, String> map = transactionsStaffRestClient.transactions(transactionDataConverter.toLedgersMockTransactions(transactions)).getBody();
        log.info("Call to Ledgers to create new transactions successful");
        return map;
    }
}
