package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.DepositAccount;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAccountsRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppAccountsRestApi.BASE_PATH)
public class TppAccountsController implements TppAccountsRestApi {
    private final AccountMapper accountMapper;
    private final AccountMgmtStaffRestClient accountMgmtStaffRestClient;

    @Override
    public ResponseEntity<Void> createAccount(String userId, DepositAccount account) {
        return accountMgmtStaffRestClient.createDepositAccountForUser(userId, accountMapper.toAccountDetailsTO(account));
    }

    @Override
    public ResponseEntity<List<AccountDetailsTO>> getAllAccounts() {
        return accountMgmtStaffRestClient.getListOfAccounts();
    }

    @Override
    public ResponseEntity<AccountDetailsTO> getSingleAccount(String accountId) {
        return accountMgmtStaffRestClient.getAccountDetailsById(accountId);
    }

    @Override
    public ResponseEntity<Void> depositCash(String accountId, AmountTO amount) {
        return accountMgmtStaffRestClient.depositCash(accountId, amount);
    }
}
