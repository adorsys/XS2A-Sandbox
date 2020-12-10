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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.*;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObaAisControllerTest {
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
    void accounts() {
        // Given
        when(aisService.getAccounts(any())).thenReturn(Collections.singletonList(getAccountDetailsTO()));

        // When
        ResponseEntity<List<AccountDetailsTO>> response = obaAisController.accounts(LOGIN);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(response.getBody()).get(0), getAccountDetailsTO());
        verify(aisService, times(1)).getAccounts(LOGIN);
    }

    @Test
    void account() {
        // Given
        when(aisService.getAccount(any())).thenReturn(getAccountDetailsTO());

        // When
        ResponseEntity<AccountDetailsTO> response = obaAisController.account(LOGIN);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(response.getBody(), getAccountDetailsTO());
        verify(aisService, times(1)).getAccount(LOGIN);
    }

    @Test
    void transactions() {
        // Given
        when(aisService.getTransactions(any(), any(), any())).thenReturn(getTransactionList());

        // When
        ResponseEntity<List<TransactionTO>> response = obaAisController.transactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now());

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(response.getBody(), getTransactionList());
        verify(aisService, times(1)).getTransactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now());
    }

    @Test
    void transactionsPaged() {
        // Given
        when(aisService.getTransactions(any(), any(), any(), anyInt(), anyInt())).thenReturn(new CustomPageImpl<>());

        // When
        ResponseEntity<CustomPageImpl<TransactionTO>> response = obaAisController.transactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now(), 0, 15);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(aisService, times(1)).getTransactions(ACCOUNT_ID, LocalDate.now().minusDays(2), LocalDate.now(), 0, 15);
    }

    @Test
    void getPendingPeriodicPayments() {
        // Given
        CustomPageImpl<PaymentTO> page = new CustomPageImpl<>(1, 1, 1, 1, 1, false, true, false, true, Collections.singletonList(getPaymentTO()));
        when(paymentRestClient.getPendingPeriodicPaymentsPaged(anyInt(),anyInt())).thenReturn(ResponseEntity.ok(page));

        // When
        ResponseEntity<CustomPageImpl<PaymentTO>> response = obaAisController.getPendingPeriodicPayments(1, 25);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(Objects.requireNonNull(response.getBody()).getContent().get(0), getPaymentTO());
        verify(paymentRestClient, times(1)).getPendingPeriodicPaymentsPaged(anyInt(),anyInt());
    }

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, CASH, ENABLED, null, null, UsageTypeTO.PRIV, null, null, false, false, null);
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
