package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAccountsRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppAccountsRestApi.BASE_PATH)
public class TppAccountsController implements TppAccountsRestApi {
    private final AccountMgmtStaffRestClient accountMgmtStaffRestClient;

    @Override
    public ResponseEntity<Void> createAccount(String userId, AccountDetailsTO account) {
        return accountMgmtStaffRestClient.createDepositAccountForUser(userId, account);
    }

    @Override
    public ResponseEntity<List<AccountDetailsTO>> getAllAccounts() {
        return accountMgmtStaffRestClient.getListOfAccounts();
    }
}
