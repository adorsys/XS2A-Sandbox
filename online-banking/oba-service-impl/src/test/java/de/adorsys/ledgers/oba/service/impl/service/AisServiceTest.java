package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AisServiceTest {
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    @InjectMocks
    private AisServiceImpl aisService;

    @Mock
    private AccountRestClient accountRestClient;

    @Test
    void getAccounts() {
        // Given
        when(accountRestClient.getListOfAccounts()).thenReturn(getAccountList());

        // When
        List<AccountDetailsTO> result = aisService.getAccounts("userLogin");

        // Then
        assertThat(result).isEqualTo(getAccountList().getBody());
    }

    @Test
    void getAccounts_bad_request() {
        // Given
        when(accountRestClient.getListOfAccounts()).thenThrow(FeignException.class);

        // Then
        assertThrows(ObaException.class, () -> aisService.getAccounts("userLogin"));
    }

    @Test
    void getTransactions() {
        // Given
        when(accountRestClient.getTransactionByDates(any(), any(), any())).thenReturn(getTransactionList());

        // When
        List<TransactionTO> result = aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1));

        // Then
        assertThat(result).isEqualTo(getTransactionList().getBody());
    }

    @Test
    void getTransactionByDates_bad_request() {
        // Given
        when(accountRestClient.getTransactionByDates(any(), any(), any())).thenThrow(FeignException.class);
        LocalDate from = LocalDate.of(2019, 1, 1);
        LocalDate to = LocalDate.of(2020, 1, 1);
        // Then
        assertThrows(ObaException.class, () -> aisService.getTransactions("Account id", from, to));
    }

    @Test
    void getTransactions_paged() {
        // Given
        when(accountRestClient.getTransactionByDatesPaged(anyString(), any(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(getTransactionTOCustomPage()));

        // When
        CustomPageImpl<TransactionTO> result = aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1), 0, 10);

        // Then
        assertEquals(0, result.getNumber());
        assertThat(result.getContent()).isEqualTo(getTransactionList().getBody());
    }

    @Test
    void getTransactions_paged_exception() {
        // Given
        when(accountRestClient.getTransactionByDatesPaged(anyString(), any(), any(), anyInt(), anyInt())).thenThrow(FeignException.class);
        LocalDate from = LocalDate.of(2019, 1, 1);
        LocalDate to = LocalDate.of(2020, 1, 1);
        // Then
        assertThrows(ObaException.class, () -> aisService.getTransactions("Account id", from, to, 0, 10));
    }

    @Test
    void getAccount_test() {
        // Given
        when(accountRestClient.getAccountDetailsById(anyString())).thenReturn(ResponseEntity.ok(new AccountDetailsTO()));

        // When
        AccountDetailsTO result = aisService.getAccount("1");

        // Then
        assertThat(result).isEqualTo(new AccountDetailsTO());
    }

    @Test
    void getAccount_test_exception() {
        // Given
        when(accountRestClient.getAccountDetailsById(anyString())).thenThrow(FeignException.class);

        // Then
        assertThrows(ObaException.class, () -> aisService.getAccount("1"));
    }

    private CustomPageImpl<TransactionTO> getTransactionTOCustomPage() {
        return new CustomPageImpl<>(0, 1, 1, 1, 1, false, true, false, true, getTransactionList().getBody());
    }

    private ResponseEntity<List<AccountDetailsTO>> getAccountList() {
        return ResponseEntity.ok(Arrays.asList(getAccount(), getAccount()));
    }

    private ResponseEntity<List<TransactionTO>> getTransactionList() {
        return ResponseEntity.ok(new ArrayList<>());
    }

    private AccountDetailsTO getAccount() {
        String IBAN = "DE1263";
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, CASH, ENABLED, null, null, UsageTypeTO.PRIV, null, null, false, false, BigDecimal.ZERO, null);
    }
}
