package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Currency;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TppDataTest {
    private static final String TPP_ID = "DE_11111111";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final String USER_ID = "QWERTY";
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    @Test
    void tppConstructorTest_success() {
        // Given
        TppData result = new TppData(new UserTO(null, null, null, null, null, getAccountAccess(), null, TPP_ID, false, false));

        // Then
        assertNotNull(result);
    }

    @Test
    void tppConstructorTest_empty_access() {
        UserTO user = new UserTO(null, null, null, null, null, null, null, null, false, false);
        // Then
        assertThrows(TppException.class, () -> new TppData(user));
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
