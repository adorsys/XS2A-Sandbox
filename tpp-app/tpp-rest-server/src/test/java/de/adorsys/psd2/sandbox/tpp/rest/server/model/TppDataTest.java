/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

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
