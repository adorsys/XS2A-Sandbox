package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class TppDataTest {
    private static final String TPP_ID = "DE_11111111";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final String USER_ID = "QWERTY";
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    @Test
    public void tppConstructorTest_success() {
        TppData result = new TppData(new UserTO(null, null, null, null, null, getAccountAccess(), null, TPP_ID));
        assertThat(result).isNotNull();
    }

    @Test(expected = TppException.class)
    public void tppConstructorTest_empty_access() {
        new TppData(new UserTO(null, null, null, null, null, null, null, null));
    }

    private List<AccountAccessTO> getAccountAccess() {
        AccountAccessTO accountAccess = new AccountAccessTO();
        accountAccess.setCurrency(CURRENCY);
        accountAccess.setAccessType(AccessTypeTO.OWNER);
        accountAccess.setIban(USER_IBAN);
        accountAccess.setScaWeight(50);
        accountAccess.setId(USER_ID);
        return Collections.singletonList(accountAccess);
    }
}
