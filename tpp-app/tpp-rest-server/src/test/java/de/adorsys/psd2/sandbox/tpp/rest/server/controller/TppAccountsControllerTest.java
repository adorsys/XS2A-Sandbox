package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.*;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.AccountMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TppAccountsControllerTest {
    private static final String USER_LOGIN = "TEST";
    private static final String USER_ID = "o2SA3pHkRqYpnHkGYGfJ_s";
    private static final String ACCOUNT_ID = "iZ4HeU0hQ-4vAqi8w7GD7Y";
    private static final String IBAN = "DE32760700240271232100";
    private static final Currency EUR = Currency.getInstance("EUR");

    @InjectMocks
    private TppAccountsController accountsController;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountMgmtStaffRestClient accountMgmtStaffRestClient;
    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;
    @Mock
    private DownloadResourceService downloadResourceService;
    @Mock
    private AccountRestClient accountRestClient;

    @Test
    public void createAccount() {
        //given
        when(accountMapper.toAccountDetailsTO(any())).thenReturn(getAccountDetailsTO());
        when(accountMgmtStaffRestClient.createDepositAccountForUser(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = accountsController.createAccount(USER_ID, getDepositAccount());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(accountMapper, times(1)).toAccountDetailsTO(getDepositAccount());
    }

    @Test
    public void updateAccountAccess() {
        //given
        when(accountMapper.toAccountAccessTO(any())).thenReturn(getAccountAccessTO());
        when(userMgmtStaffRestClient.updateAccountAccessForUser(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = accountsController.updateAccountAccess(getAccountAccess());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(accountMapper, times(1)).toAccountAccessTO(getAccountAccess());
    }

    @Test
    public void getAllAccounts() {
        //given
        when(accountMgmtStaffRestClient.getListOfAccounts()).thenAnswer(i -> ResponseEntity.ok(Collections.singletonList(getAccountDetailsTO())));

        //when
        ResponseEntity<List<AccountDetailsTO>> response = accountsController.getAllAccounts();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), getAccountDetailsTO());
    }

    @Test
    public void getAllAccountsPaged() {
        //given
        when(accountMgmtStaffRestClient.getListOfAccountsPaged(any(), anyInt(), anyInt())).thenAnswer(i -> ResponseEntity.ok(getCustomPageImpl()));

        //when
        ResponseEntity<CustomPageImpl<AccountDetailsTO>> response = accountsController.getAllAccounts(IBAN, 0, 25);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void getAccountDetailsByIban() {
        //given
        when(accountRestClient.getAccountDetailsByIban(any())).thenAnswer(i -> ResponseEntity.ok(getAccountDetailsTO()));

        //when
        ResponseEntity<AccountDetailsTO> response = accountsController.getAccountDetailsByIban(IBAN);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountDetailsTO());
    }

    @Test
    public void getSingleAccount() {
        //given
        when(accountMgmtStaffRestClient.getAccountDetailsById(any())).thenAnswer(i -> ResponseEntity.ok(getAccountDetailsTO()));

        //when
        ResponseEntity<AccountDetailsTO> response = accountsController.getSingleAccount(ACCOUNT_ID);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountDetailsTO());
    }

    @Test
    public void accountReport() {
        //given
        when(accountMgmtStaffRestClient.getExtendedAccountDetailsById(any())).thenReturn(ResponseEntity.ok(getAccountReportTO()));
        when(accountMapper.toAccountReport(any())).thenReturn(getAccountReport());

        //when
        ResponseEntity<AccountReport> response = accountsController.accountReport(ACCOUNT_ID);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountReport());
    }

    @Test
    public void depositCash() {
        //given
        when(accountMgmtStaffRestClient.depositCash(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        //when
        ResponseEntity<Void> response = accountsController.depositCash(ACCOUNT_ID, getAmountTO());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void downloadAccountTemplate() {
        //given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(null);

        //when
        ResponseEntity<Resource> response = accountsController.downloadAccountTemplate();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    private AmountTO getAmountTO() {
        return new AmountTO(EUR, BigDecimal.ONE);
    }

    private AccountReportTO getAccountReportTO() {
        return new AccountReportTO(getAccountDetailsTO(), Collections.singletonList(getUserTO()));
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, USER_LOGIN, "email", "pin", null, null, null, "branch");
    }

    private AccountReport getAccountReport() {
        return new AccountReport(getAccountDetailsTO(), Collections.singletonList(getUserAccess()));
    }

    private UserAccess getUserAccess() {
        return new UserAccess(USER_LOGIN, 100, AccessType.OWNER);
    }

    private AccountAccess getAccountAccess() {
        return new AccountAccess("id", IBAN, EUR, AccessTypeTO.OWNER, 100, ACCOUNT_ID);
    }

    private AccountAccessTO getAccountAccessTO() {
        return new AccountAccessTO("id", IBAN, EUR, AccessTypeTO.OWNER, 100, ACCOUNT_ID);
    }

    private DepositAccount getDepositAccount() {
        return new DepositAccount("id", AccountType.CASH, AccountUsage.PRIV, EUR, IBAN, AccountStatus.ENABLED);
    }

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO("id", IBAN, "bban", "pan", "maskedPan", "msisdn", EUR, "name", "product", AccountTypeTO.CASH, AccountStatusTO.ENABLED,
                                    "bic", "linkedAccounts", UsageTypeTO.PRIV, "details", Collections.singletonList(new AccountBalanceTO()));
    }

    private CustomPageImpl<AccountDetailsTO> getCustomPageImpl() {
        return new CustomPageImpl<AccountDetailsTO>();
    }
}
