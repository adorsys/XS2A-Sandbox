package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

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
    private static final String BRANCH_ID = "MY-TEST-BRANCH";
    private static final String ACC_ID = "ZXCVASDF";
    private static final String IBAN = "DE1234567890";
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void userToUserTOTest() {
        User user = new User();
        user.setEmail("vne@adorsys.de");
        user.setLogin("vne");
        user.setPin("12345");
        user.setId("12345678");

        UserTO userTO = userMapper.toUserTO(user);

        Assert.assertEquals(userTO.getEmail(), user.getEmail());
        Assert.assertEquals(userTO.getLogin(), user.getLogin());
        Assert.assertEquals(userTO.getPin(), user.getPin());
    }

    @Test
    public void toUserTO() {
        User input = createUser();
        UserTO expected = createUserTO();
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private UserTO createUserTO() {
        ScaUserDataTO scaUserDataTO = new ScaUserDataTO(SCA_ID, ScaMethodTypeTO.EMAIL, EMAIL, null, true, STATIC_TAN, false);
        return new UserTO(USER_ID, USER_LOGIN, EMAIL, PIN, singletonList(scaUserDataTO), singletonList(new AccountAccessTO(ACC_ID, IBAN, OWNER, 50)), singletonList(CUSTOMER), null);
    }

    private User createUser() {
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
        user.setScaUserData(singletonList(scaEmail));

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
        access.setScaWeight(50);
        access.setAccessType(OWNER);
        return access;
    }
}
