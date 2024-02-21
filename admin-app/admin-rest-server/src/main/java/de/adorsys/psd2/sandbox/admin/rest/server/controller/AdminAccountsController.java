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

package de.adorsys.psd2.sandbox.admin.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.AccountReport;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.DepositAccount;
import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminAccountsRestApi;
import de.adorsys.psd2.sandbox.admin.rest.server.mapper.AccountMapper;
import de.adorsys.psd2.sandbox.admin.rest.server.service.DownloadResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AdminAccountsRestApi.BASE_PATH)
public class AdminAccountsController implements AdminAccountsRestApi {
    private static final String FILE_NAME = "data_payload_template.yml";

    private final AccountMapper accountMapper;
    private final AccountMgmtStaffRestClient accountMgmtStaffRestClient;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final DownloadResourceService downloadResourceService;

    @Override
    public ResponseEntity<Boolean> createAccount(String userId, DepositAccount account) {
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
    public ResponseEntity<CustomPageImpl<AccountDetailsTO>> getAllAccounts(String queryParam, int page, int size, boolean withBalance) {
        return accountMgmtStaffRestClient.getListOfAccountsPaged(queryParam, page, size, withBalance);
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

    @Override
    public ResponseEntity<Void> updateCreditLimit(String accountId, BigDecimal creditAmount) {
        return accountMgmtStaffRestClient.changeCreditLimit(accountId, creditAmount);
    }
}
