package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.*;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO.OWNER;
import static de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO.CUSTOMER;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
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
    public void toUserTO() {
        User user = new User();
        user.setEmail("test@mail.de");
        user.setLogin("test");
        user.setPin("12345");
        user.setId("12345678");

        UserTO userTO = userMapper.toUserTO(user);

        Assert.assertEquals(userTO.getEmail(), user.getEmail());
        Assert.assertEquals(userTO.getLogin(), user.getLogin());
        Assert.assertEquals(userTO.getPin(), user.getPin());
    }

    @Test
    public void toUserTO_null_collection_should_become_empty() {
        User input = createUser(true);
        UserTO expected = createUserTO(true);
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void toUserTO_all_filled() {
        User input = createUser(false);
        UserTO expected = createUserTO(false);
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private UserTO createUserTO(boolean emptySca) {
        ScaUserDataTO scaUserDataTO = new ScaUserDataTO(SCA_ID, ScaMethodTypeTO.EMAIL, EMAIL, null, true, STATIC_TAN, false, false);
        return new UserTO(USER_ID, USER_LOGIN, EMAIL, PIN, emptySca ? Collections.emptyList() : singletonList(scaUserDataTO), singletonList(new AccountAccessTO(ACC_ID, IBAN, CURRENCY, OWNER, 50, DEPOSIT_ACC_ID)), singletonList(CUSTOMER), null);
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
        scaEmail.setScaMethod(ScaMethodType.EMAIL);
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
