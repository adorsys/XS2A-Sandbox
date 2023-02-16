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

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO.OWNER;
import static de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO.CUSTOMER;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UserMapperTest {
    private static final String USER_ID = "USER-ID";
    private static final String USER_LOGIN = "USER-LOGIN";
    private static final String EMAIL = "EMAIL";
    private static final String PIN = "PIN";
    private static final String SCA_ID = "SCA-ID";
    private static final String STATIC_TAN = "12345";
    private static final String ACC_ID = "ZXCVASDF";
    private static final String DEPOSIT_ACC_ID = "123ACHkr4J";
    private static final String IBAN = "DE1234567890";
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toUserTO() {
        // Given
        User user = new User();
        user.setEmail("test@mail.de");
        user.setLogin("test");
        user.setPin("12345");
        user.setId("12345678");

        // When
        UserTO userTO = userMapper.toUserTO(user);

        // Then
        assertEquals(userTO.getEmail(), user.getEmail());
        assertEquals(userTO.getLogin(), user.getLogin());
        assertEquals(userTO.getPin(), user.getPin());
    }

    @Test
    void toUserTO_null_collection_should_become_empty() {
        // Given
        User input = createUser(true);
        UserTO expected = createUserTO(true);

        // When
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    void toUserTO_all_filled() {
        // Given
        User input = createUser(false);
        UserTO expected = createUserTO(false);

        // When
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private UserTO createUserTO(boolean emptySca) {
        ScaUserDataTO scaUserDataTO = new ScaUserDataTO(SCA_ID, ScaMethodTypeTO.SMTP_OTP, EMAIL, null, true, STATIC_TAN, false, false);
        return new UserTO(USER_ID, USER_LOGIN, EMAIL, PIN, emptySca ? Collections.emptyList() : singletonList(scaUserDataTO), singletonList(new AccountAccessTO(ACC_ID, IBAN, CURRENCY, OWNER, 50, DEPOSIT_ACC_ID)), singletonList(CUSTOMER), null, false, false);
    }

    private User createUser(boolean emptySca) {
        User user = new User();
        user.setId(USER_ID);
        user.setEmail(EMAIL);
        user.setLogin(USER_LOGIN);
        user.setPin(PIN);

        // SCA EMAIL
        ScaUserData scaEmail = new ScaUserData();
        scaEmail.setId(SCA_ID);
        scaEmail.setMethodValue(EMAIL);
        scaEmail.setScaMethod(ScaMethodType.SMTP_OTP);
        scaEmail.setStaticTan(STATIC_TAN);
        scaEmail.setUsesStaticTan(true);
        scaEmail.setValid(false);
        user.setScaUserData(emptySca
                                ? null
                                : singletonList(scaEmail));

        //Set AccountAccess
        user.setAccountAccesses(singletonList(getAccountAccess()));

        // Assign all roles to the user
        user.setUserRoles(singletonList(UserRole.CUSTOMER));
        return user;
    }

    private AccountAccess getAccountAccess() {
        AccountAccess access = new AccountAccess();
        access.setId(ACC_ID);
        access.setIban(IBAN);
        access.setCurrency(CURRENCY);
        access.setScaWeight(50);
        access.setAccessType(OWNER);
        access.setAccountId(DEPOSIT_ACC_ID);
        return access;
    }
}
