package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;

import java.time.LocalDate;
import java.util.List;

public interface AisService {
    List<AccountDetailsTO> getAccounts(String userLogin);

    List<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo);

    CustomPageImpl<TransactionTO> getTransactions(String accountId, LocalDate dateFrom, LocalDate dateTo, int page, int size);

    AccountDetailsTO getAccount(String accountId);
}
