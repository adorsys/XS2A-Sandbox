package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DataPayloadTest {

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

    private DataPayload getData(boolean emptyUsers, boolean nullAccounts, boolean nullElementInBalances) {
        List<UserTO> users = emptyUsers
                                 ? Collections.emptyList()
                                 : Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = nullAccounts
                                              ? null
                                              : Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = nullElementInBalances
                                            ? Collections.singletonList(null)
                                            : Collections.singletonList(new AccountBalance());
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, "12345", new HashMap<>());
    }
}
