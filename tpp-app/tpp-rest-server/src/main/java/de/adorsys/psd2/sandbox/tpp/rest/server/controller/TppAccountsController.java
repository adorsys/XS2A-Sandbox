package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountReport;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.DepositAccount;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAccountsRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.AccountMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppAccountsRestApi.BASE_PATH)
public class TppAccountsController implements TppAccountsRestApi {
    private static final String FILE_NAME = "data_payload_template.yml";

    private final AccountMapper accountMapper;
    private final AccountMgmtStaffRestClient accountMgmtStaffRestClient;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final DownloadResourceService downloadResourceService;
    private final AccountRestClient accountRestClient;

    @Override
    public ResponseEntity<Void> createAccount(String userId, DepositAccount account) {
        return accountMgmtStaffRestClient.createDepositAccountForUser(userId, accountMapper.toAccountDetailsTO(account));
    }

    @Override
    public ResponseEntity<Void> updateAccountAccess(AccountAccess accountAccess) {
        return userMgmtStaffRestClient.updateAccountAccessForUser(accountAccess.getId(), accountMapper.toAccountAccessTO(accountAccess));
    }

    @Override
    public ResponseEntity<List<AccountDetailsTO>> getAllAccounts() {
        return accountMgmtStaffRestClient.getListOfAccounts();
    }

    @Override
    public ResponseEntity<CustomPageImpl<AccountDetailsTO>> getAllAccounts(String queryParam, int page, int size) {
        return accountMgmtStaffRestClient.getListOfAccountsPaged(queryParam, page, size);
    }

    @Override
    public ResponseEntity<AccountDetailsTO> getAccountDetailsByIban(String iban) {
        return accountRestClient.getAccountDetailsByIban(iban);
    }

    @Override
    public ResponseEntity<AccountDetailsTO> getSingleAccount(String accountId) {
        return accountMgmtStaffRestClient.getAccountDetailsById(accountId);
    }

    @Override
    public ResponseEntity<AccountReport> accountReport(String accountId) {
        return ResponseEntity.ok(accountMapper.toAccountReport(accountMgmtStaffRestClient.getExtendedAccountDetailsById(accountId).getBody()));
    }

    @Override
    public ResponseEntity<Void> depositCash(String accountId, AmountTO amount) {
        return accountMgmtStaffRestClient.depositCash(accountId, amount);
    }

    @Override
    public ResponseEntity<Resource> downloadAccountTemplate() {
        return ResponseEntity.ok()
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME)
                   .body(downloadResourceService.getResourceByTemplate(FILE_NAME));
    }

    @Override
    public ResponseEntity<Boolean> changeStatus(String accountId) {
        return accountMgmtStaffRestClient.changeStatus(accountId);
    }
}
