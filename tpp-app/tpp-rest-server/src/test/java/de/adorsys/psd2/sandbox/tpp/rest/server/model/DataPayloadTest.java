package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DataPayloadTest {
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency EUR = Currency.getInstance("EUR");
    @InjectMocks
    private IbanGenerationService service;
    @Mock
    private UserMgmtRestClient restClient;


    @Test
    public void isValidPayload_valid() {
        DataPayload data = getData(false, false, false);
        boolean result = data.isValidPayload();
        assertThat(result).isTrue();
    }

    @Test
    public void isValidPayloadValid_emptyUsers() {
        DataPayload data = getData(true, false, false);
        boolean result = data.isValidPayload();
        assertThat(result).isTrue();
    }

    @Test
    public void isValidPayloadValid_nullAccounts() {
        DataPayload data = getData(false, true, false);
        boolean result = data.isValidPayload();
        assertThat(result).isTrue();
        assertThat(data.getAccounts()).isNotNull();
    }

    @Test
    public void isValidPayloadValid_nullElementBalance() {
        DataPayload data = getData(false, false, true);
        boolean result = data.isValidPayload();
        assertThat(result).isFalse();
    }

    @Test
    public void updatePayload() {
        when(restClient.getUser()).thenReturn(ResponseEntity.ok(getBranch()));
        DataPayload data = getData(false, false, false);
        data.setPayments(Collections.emptyList());
        data.updatePayload(service::generateIbanForNisp, "DE_12345678", EUR, false);
        assertThat(data).isNotNull();
        assertThat(data.getAccounts().get(0).getIban()).isEqualTo("DE87123456780000000002");
        assertThat(data.getAccounts().get(0).getCurrency()).isEqualTo(EUR);
        assertThat(data.getUsers().get(0).getAccountAccesses().get(0).getIban()).isEqualTo("DE87123456780000000002");
        assertThat(data.getUsers().get(0).getAccountAccesses().get(0).getCurrency()).isEqualTo(EUR);
        assertThat(data.getBalancesList().get(0).getIban()).isEqualTo("DE87123456780000000002");
        assertThat(data.getBalancesList().get(0).getCurrency()).isEqualTo(EUR);
    }

    private UserTO getBranch() {
        return new UserTO(null, null, null, null, null, Collections.singletonList(new AccountAccessTO(null, "DE17123456780000000001", USD, AccessTypeTO.OWNER, 100, null)), null, "DE_12345678");
    }

    private DataPayload getData(boolean emptyUsers, boolean nullAccounts, boolean nullElementInBalances) {
        List<UserTO> users = emptyUsers
                                 ? Collections.emptyList()
                                 : Collections.singletonList(getUser());
        List<AccountDetailsTO> accounts = nullAccounts
                                              ? null
                                              : Collections.singletonList(getDetails());
        List<AccountBalance> balances = nullElementInBalances
                                            ? Collections.singletonList(null)
                                            : Collections.singletonList(getBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, "DE_12345678", new HashMap<>());
    }

    private UserTO getUser() {
        UserTO user = new UserTO("login", "email", "pin");
        user.setAccountAccesses(Collections.singletonList(new AccountAccessTO(null, "02", USD, null, 100, null)));
        return user;
    }

    private AccountBalance getBalance() {
        return new AccountBalance(null, "02", USD, BigDecimal.ZERO);
    }

    private AccountDetailsTO getDetails() {
        AccountDetailsTO details = new AccountDetailsTO();
        details.setIban("02");
        details.setCurrency(USD);
        return details;
    }
}
