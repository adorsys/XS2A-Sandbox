/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataPayloadTest {
    private static final Currency USD = Currency.getInstance("USD");
    public static final Currency EUR = Currency.getInstance("EUR");
    @InjectMocks
    private IbanGenerationService service;
    @Mock
    private UserMgmtRestClient restClient;


    @Test
    void isValidPayload_valid() {
        // Given
        DataPayload data = getData(false, false, false);
        boolean result = data.isValidPayload();

        // Then
        assertTrue(result);
    }

    @Test
    void isValidPayloadValid_emptyUsers() {
        // Given
        DataPayload data = getData(true, false, false);
        boolean result = data.isValidPayload();

        // Then
        assertTrue(result);
    }

    @Test
    void isValidPayloadValid_nullAccounts() {
        // Given
        DataPayload data = getData(false, true, false);
        boolean result = data.isValidPayload();

        // Then
        assertTrue(result);
        assertNotNull(data.getAccounts());
    }

    @Test
    void isValidPayloadValid_nullElementBalance() {
        // Given
        DataPayload data = getData(false, false, true);
        boolean result = data.isValidPayload();

        // Then
        assertFalse(result);
    }

    @Test
    void updatePayload() {
        // Given
        when(restClient.getUser()).thenReturn(ResponseEntity.ok(getBranch()));
        DataPayload data = getData(false, false, false);
        data.setPayments(Collections.emptyList());

        // When
        data.updatePayload(service::generateIbanForNisp, "DE_12345678", EUR, false);

        // Then
        assertNotNull(data);
        assertEquals("DE87123456780000000002", data.getAccounts().get(0).getIban());
        assertEquals(EUR, data.getAccounts().get(0).getCurrency());
        assertEquals("DE87123456780000000002", data.getUsers().get(0).getAccountAccesses().get(0).getIban());
        assertEquals(EUR, data.getUsers().get(0).getAccountAccesses().get(0).getCurrency());
        assertEquals("DE87123456780000000002", data.getBalancesList().get(0).getIban());
        assertEquals(EUR, data.getBalancesList().get(0).getCurrency());
    }

    private UserTO getBranch() {
        return new UserTO(null, null, null, null, null, Collections.singletonList(new AccountAccessTO(null, "DE17123456780000000001", USD, AccessTypeTO.OWNER, 100, null)), null, "DE_12345678", false, false);
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
        List<PaymentTO> payments = Collections.singletonList(new PaymentTO());
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
