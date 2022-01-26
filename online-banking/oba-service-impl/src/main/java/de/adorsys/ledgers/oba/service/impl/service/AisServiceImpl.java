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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.AisService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AisServiceImpl implements AisService {
    private static final String RESPONSE_ERROR = "Error in response from Ledgers, please contact admin.";
    private static final String GET_ACCOUNTS_ERROR_MSG = "Failed to retrieve accounts for user: %s, code: %s, message: %s";
    private static final String GET_TRANSACTIONS_ERROR_MSG = "Failed to retrieve transactions for account: %s, code: %s, message: %s";
    private static final String GET_ACCOUNT_ERROR_MSG = "Failed to retrieve account by id: %s, code: %s, message: %s";

    private final AccountRestClient accountRestClient;

    @Override
    public List<AccountDetailsTO> getAccounts(String userLogin) {
        try {
            return Optional.ofNullable(accountRestClient.getListOfAccounts().getBody())
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = String.format(GET_ACCOUNTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public List<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo) {
        try {
            return Optional.ofNullable(accountRestClient.getTransactionByDates(accountId, dateFrom, dateTo).getBody())
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = String.format(GET_TRANSACTIONS_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public CustomPageImpl<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        try {
            return accountRestClient.getTransactionByDatesPaged(accountId, dateFrom, dateTo, page, size).getBody();
        } catch (FeignException e) {
            String msg = String.format(GET_TRANSACTIONS_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public AccountDetailsTO getAccount(String accountId) {
        try {
            return accountRestClient.getAccountDetailsById(accountId).getBody();
        } catch (FeignException e) {
            String msg = String.format(GET_ACCOUNT_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build();
        }
    }
}
