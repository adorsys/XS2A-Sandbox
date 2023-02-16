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

package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BalanceMapperTest {
    private static final String ACCOUNT_ID = "ACCOUNT ID";
    private static final String IBAN = "DE12345678";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final BalanceMapper mapper = Mappers.getMapper(BalanceMapper.class);

    @Test
    void toAccountBalanceTO() {
        // When
        AccountBalanceTO result = mapper.toAccountBalanceTO(getBalance());

        // Then
        assertEquals(getAccountBalanceTo(), result);
    }

    private AccountBalanceTO getAccountBalanceTo() {
        return new AccountBalanceTO(new AmountTO(EUR, AMOUNT), null, null, null, null, IBAN);
    }

    private AccountBalance getBalance() {
        return new AccountBalance(ACCOUNT_ID, IBAN, EUR, AMOUNT);
    }
}
