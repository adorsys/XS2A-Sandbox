package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.TransactionStatusTO;
import de.adorsys.ledgers.middleware.client.rest.PaymentRestClient;
import de.adorsys.ledgers.oba.service.api.service.AisService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ObaAisControllerTest {
    private static final String LOGIN = "anton.brueckner";
    private static final String IBAN = "DE1234567890";
    private static final String PAYMENT_ID = "o2SA3pHkRqYpnHkGYGfJ_s";
    private static final String ACCOUNT_ID = "iZ4HeU0hQ-4vAqi8w7GD7Y";
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    @InjectMocks
    private ObaAisController obaAisController;
    @Mock
    private AisService aisService;
    @Mock
    private PaymentRestClient paymentRestClient;

    @Test
    public void accounts() {
        //given
        when(aisService.getAccounts(any())).thenReturn(Collections.singletonList(getAccountDetailsTO()));

        //when
        ResponseEntity<List<AccountDetailsTO>> response = obaAisController.accounts(LOGIN);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), getAccountDetailsTO());
        verify(aisService, times(1)).getAccounts(LOGIN);
    }

    @Test
    public void account() {
        //given
        when(aisService.getAccount(any())).thenReturn(getAccountDetailsTO());

        //when
        ResponseEntity<AccountDetailsTO> response = obaAisController.account(LOGIN);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getAccountDetailsTO());
        verify(aisService, times(1)).getAccount(LOGIN);
    }

    @Test
    public void transactions() {
        //given
        when(aisService.getTransactions(any(), any(), any())).thenReturn(getTransactionList());

        //when
        ResponseEntity<List<TransactionTO>> response = obaAisController.transactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now());

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(response.getBody(), getTransactionList());
        verify(aisService, times(1)).getTransactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now());
    }

    @Test
    public void transactionsPaged() {
        //given
        when(aisService.getTransactions(any(), any(), any(), anyInt(), anyInt())).thenReturn(new CustomPageImpl<>());

        //when
        ResponseEntity<CustomPageImpl<TransactionTO>> response = obaAisController.transactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now(), 0, 15);

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        verify(aisService, times(1)).getTransactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now(), 0, 15);
    }

    @Test
    public void getPendingPeriodicPayments() {
        //given
        when(paymentRestClient.getPendingPeriodicPayments()).thenReturn(ResponseEntity.ok(Collections.singletonList(getPaymentTO())));

        //when
        ResponseEntity<List<PaymentTO>> response = obaAisController.getPendingPeriodicPayments();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), getPaymentTO());
        verify(paymentRestClient, times(1)).getPendingPeriodicPayments();
    }

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, CASH, ENABLED, null, null, UsageTypeTO.PRIV, null, null);
    }

    private List<TransactionTO> getTransactionList() {
        return new ArrayList<>();
    }

    private PaymentTO getPaymentTO() {
        PaymentTO payment = new PaymentTO();
        payment.setPaymentId(PAYMENT_ID);
        payment.setPaymentType(PaymentTypeTO.SINGLE);
        payment.setPaymentProduct("sepa");
        payment.setTransactionStatus(TransactionStatusTO.ACSP);
        return payment;
    }
}
