package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import feign.FeignException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AisServiceTest {
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private final String IBAN = "DE1263";
    @InjectMocks
    private AisServiceImpl aisService;

    @Mock
    private AccountRestClient accountRestClient;

    @Test
    public void getAccounts() {
        when(accountRestClient.getListOfAccounts()).thenReturn(getAccountList());
        List<AccountDetailsTO> result = aisService.getAccounts("userLogin");
        assertThat(result).isEqualTo(getAccountList().getBody());
    }

    @Test(expected = ObaException.class)
    public void getAccounts_bad_request() {
        when(accountRestClient.getListOfAccounts()).thenThrow(FeignException.class);
        aisService.getAccounts("userLogin");
    }

    @Test
    public void getTransactions() {
        when(accountRestClient.getTransactionByDates(any(), any(), any())).thenReturn(getTransactionList());
        List<TransactionTO> result = aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1));
        assertThat(result).isEqualTo(getTransactionList().getBody());
    }

    @Test(expected = ObaException.class)
    public void getTransactionByDates_bad_request() {
        when(accountRestClient.getTransactionByDates(any(), any(), any())).thenThrow(FeignException.class);
        aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1));

    }

    @Test
    public void getTransactions_paged() {
        when(accountRestClient.getTransactionByDatesPaged(anyString(), any(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok(getTransactionTOCustomPage()));
        CustomPageImpl<TransactionTO> result = aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1), 0, 10);
        assertThat(result.getNumber()).isEqualTo(0);
        assertThat(result.getContent()).isEqualTo(getTransactionList().getBody());
    }

    @Test(expected = ObaException.class)
    public void getTransactions_paged_exception() {
        when(accountRestClient.getTransactionByDatesPaged(anyString(), any(), any(), anyInt(), anyInt())).thenThrow(FeignException.class);
        aisService.getTransactions("Account id", LocalDate.of(2019, 1, 1), LocalDate.of(2020, 1, 1), 0, 10);
    }

    @Test
    public void getAccount_test(){
        when(accountRestClient.getAccountDetailsById(anyString())).thenReturn(ResponseEntity.ok(new AccountDetailsTO()));
        AccountDetailsTO result = aisService.getAccount("1");
        assertThat(result).isEqualTo(new AccountDetailsTO());
    }

    @Test(expected = ObaException.class)
    public void getAccount_test_exception(){
        when(accountRestClient.getAccountDetailsById(anyString())).thenThrow(FeignException.class);
         aisService.getAccount("1");
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
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, CASH, ENABLED, null, null, UsageTypeTO.PRIV, null, null);
    }
}
