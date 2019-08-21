package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.oba.rest.api.exception.AisException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.api.domain.AisErrorCode.AIS_BAD_REQUEST;

@Slf4j
@Service
@RequiredArgsConstructor
public class AisService {
    private static final String RESPONSE_ERROR = "Error in response from Ledgers, please contact admin.";
    private static final String GET_ACCOUNTS_ERROR_MSG = "Failed to retrieve accounts for user: %s, code: %s, message: %s";
    private static final String GET_TRANSACTIONS_ERROR_MSG = "Failed to retrieve transactions for account: %s, code: %s, message: %s";
    private static final String GET_ACCOUNT_ERROR_MSG = "Failed to retrieve account by id: %s, code: %s, message: %s";

    private final AccountRestClient accountRestClient;

    public List<AccountDetailsTO> getAccounts(String userLogin) {
        try {
            return Optional.ofNullable(accountRestClient.getListOfAccounts().getBody())
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = String.format(GET_ACCOUNTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AIS_BAD_REQUEST).build();
        }
    }

    public List<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo) {
        try {
            return Optional.ofNullable(accountRestClient.getTransactionByDates(accountId, dateFrom, dateTo).getBody())
                       .orElse(Collections.emptyList());
        } catch (FeignException e) {
            String msg = String.format(GET_TRANSACTIONS_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AIS_BAD_REQUEST).build();
        }
    }

    public AccountDetailsTO getAccount(String accountId) {
        try {
            return accountRestClient.getAccountDetailsById(accountId).getBody();
        } catch (FeignException e) {
            String msg = String.format(GET_ACCOUNT_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AIS_BAD_REQUEST).build();
        }
    }
}
