package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;

import static java.util.Collections.emptyList;
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
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    public void ttpInfoToUserTOTest() {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setEmail("vne@adorsys.de");
        tppInfo.setLogin("vne");
        tppInfo.setPin("12345");
        tppInfo.setId("12345678");

        UserTO user = userMapper.ttpInfoToUserTO(tppInfo);

        Assert.assertEquals(user.getEmail(), tppInfo.getEmail());
        Assert.assertEquals(user.getLogin(), tppInfo.getLogin());
        Assert.assertEquals(user.getPin(), tppInfo.getPin());
    }

    @Test
    public void toUserTO() {
        User input = createUser();
        UserTO expected = createUserTO();
        UserTO result = userMapper.toUserTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private UserTO createUserTO() {
        ScaUserDataTO scaUserDataTO = new ScaUserDataTO(SCA_ID, ScaMethodTypeTO.EMAIL, EMAIL, null, true, STATIC_TAN);
        return new UserTO(USER_ID, USER_LOGIN, EMAIL, PIN, singletonList(scaUserDataTO), emptyList(), singletonList(UserRoleTO.CUSTOMER), null);
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

        // Assign all roles to the user
        user.setUserRoles(Collections.singletonList(UserRole.CUSTOMER));
        return user;
    }
}
