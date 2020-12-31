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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TppAccountsControllerTest {
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
    void createAccount() {
        // Given
        when(accountMapper.toAccountDetailsTO(any())).thenReturn(getAccountDetailsTO());
        when(accountMgmtStaffRestClient.createDepositAccountForUser(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = accountsController.createAccount(USER_ID, getDepositAccount());

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(accountMapper, times(1)).toAccountDetailsTO(getDepositAccount());
    }

    @Test
    void updateAccountAccess() {
        // Given
        when(accountMapper.toAccountAccessTO(any())).thenReturn(getAccountAccessTO());
        when(userMgmtStaffRestClient.updateAccountAccessForUser(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = accountsController.updateAccountAccess(getAccountAccess());

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(accountMapper, times(1)).toAccountAccessTO(getAccountAccess());
    }

    @Test
    void getAllAccounts() {
        // Given
        when(accountMgmtStaffRestClient.getListOfAccounts()).thenAnswer(i -> ResponseEntity.ok(Collections.singletonList(getAccountDetailsTO())));

        // When
        ResponseEntity<List<AccountDetailsTO>> response = accountsController.getAllAccounts();

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), getAccountDetailsTO());
    }

    @Test
    void getAllAccountsPaged() {
        // Given
        when(accountMgmtStaffRestClient.getListOfAccountsPaged(any(), anyInt(), anyInt())).thenAnswer(i -> ResponseEntity.ok(getCustomPageImpl()));

        // When
        ResponseEntity<CustomPageImpl<AccountDetailsTO>> response = accountsController.getAllAccounts(IBAN, 0, 25);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void getSingleAccount() {
        // Given
        when(accountMgmtStaffRestClient.getAccountDetailsById(any())).thenAnswer(i -> ResponseEntity.ok(getAccountDetailsTO()));

        // When
        ResponseEntity<AccountDetailsTO> response = accountsController.getSingleAccount(ACCOUNT_ID);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountDetailsTO());
    }

    @Test
    void accountReport() {
        // Given
        when(accountMgmtStaffRestClient.getExtendedAccountDetailsById(any())).thenReturn(ResponseEntity.ok(getAccountReportTO()));
        when(accountMapper.toAccountReport(any())).thenReturn(getAccountReport());

        // When
        ResponseEntity<AccountReport> response = accountsController.accountReport(ACCOUNT_ID);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountReport());
    }

    @Test
    void depositCash() {
        // Given
        when(accountMgmtStaffRestClient.depositCash(any(), any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = accountsController.depositCash(ACCOUNT_ID, getAmountTO());

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void downloadAccountTemplate() {
        // Given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(null);

        // When
        ResponseEntity<Resource> response = accountsController.downloadAccountTemplate();

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void changeStatus() {
        when(accountMgmtStaffRestClient.changeStatus(anyString())).thenReturn(ResponseEntity.ok(true));
        ResponseEntity<Boolean> result = accountsController.changeStatus(ACCOUNT_ID);
        assertEquals(ResponseEntity.ok(true), result);
    }

    private AmountTO getAmountTO() {
        return new AmountTO(EUR, BigDecimal.ONE);
    }

    private AccountReportTO getAccountReportTO() {
        return new AccountReportTO(getAccountDetailsTO(), Collections.singletonList(getUserTO()));
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, USER_LOGIN, "email", "pin", null, null, null, "branch", false, false);
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
                                    "bic", "linkedAccounts", UsageTypeTO.PRIV, "details", Collections.singletonList(new AccountBalanceTO()), false, false, BigDecimal.ZERO, null);
    }

    private CustomPageImpl<AccountDetailsTO> getCustomPageImpl() {
        return new CustomPageImpl<>();
    }
}
