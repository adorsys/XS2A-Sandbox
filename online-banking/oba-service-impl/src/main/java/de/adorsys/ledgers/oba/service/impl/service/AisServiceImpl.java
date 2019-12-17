package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.AisErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.AisException;
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
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AisErrorCode.AIS_BAD_REQUEST).build();
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
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AisErrorCode.AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public CustomPageImpl<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo, int page, int size) {
        try {
            return accountRestClient.getTransactionByDatesPaged(accountId, dateFrom, dateTo, page, size).getBody();
        } catch (FeignException e) {
            String msg = String.format(GET_TRANSACTIONS_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AisErrorCode.AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public AccountDetailsTO getAccount(String accountId) {
        try {
            return accountRestClient.getAccountDetailsById(accountId).getBody();
        } catch (FeignException e) {
            String msg = String.format(GET_ACCOUNT_ERROR_MSG, accountId, e.status(), e.getMessage());
            log.error(msg);
            throw AisException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .aisErrorCode(AisErrorCode.AIS_BAD_REQUEST).build();
        }
    }
}
