package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.ScaUserData;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserRole;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TppUsersControllerTest {
    private static final UserRoleTO USER_ROLE = UserRoleTO.CUSTOMER;
    private static final String USER_ID = "USER_ID";
    private static final String EMAIL = "EMAIL";
    private static final String LOGIN = "LOGIN";
    private static final String BRANCH = "BRANCH";

    @InjectMocks
    private TppUsersController tppUsersController;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;

    @Test
    public void createUser() {
        //given
        when(userMapper.toUserTO(any())).thenReturn(getUserTO(BRANCH));
        when(userMgmtStaffRestClient.createUser(any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        //when
        ResponseEntity<UserTO> user = tppUsersController.createUser(getUser(USER_ID));

        //then
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(Objects.requireNonNull(user.getBody()).getId()).isEqualTo(getUserTO(BRANCH).getId());
    }

    @Test
    public void getAllUsers() {
        //given
        when(userMgmtStaffRestClient.getBranchUsersByRoles(Collections.singletonList(USER_ROLE), LOGIN, 0, 25)).thenReturn(ResponseEntity.ok(getCustomPageImplUserTO()));

        //when
        ResponseEntity<CustomPageImpl<UserTO>> user = tppUsersController.getAllUsers(LOGIN, 0, 25);

        //then
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        assertEquals(user.getBody(), getCustomPageImplUserTO());
    }

    @Test
    public void updateUser() {
        //given
        when(userMapper.toUserTO(any())).thenReturn(getUserTO(BRANCH));
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));
        when(userMgmtStaffRestClient.modifyUser(any(), any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        //when
        ResponseEntity<Void> user = tppUsersController.updateUser(getUser(USER_ID));

        //then
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test(expected = TppException.class)
    public void updateUser_userIdNull() {
        //when
        tppUsersController.updateUser(getUser(null));
    }

    @Test(expected = TppException.class)
    public void updateUser_tppCodedNull() {
        //given
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO(null)));

        //when
        tppUsersController.updateUser(getUser(USER_ID));
    }

    @Test
    public void getUser() {
        //given
        when(userMgmtRestClient.getUserById(any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        //when
        ResponseEntity<UserTO> user = tppUsersController.getUser(USER_ID);

        //then
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(user.getBody().getId()).isEqualTo(USER_ID);
    }

    @Test
    public void getSelf() {
        //given
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        //when
        ResponseEntity<UserTO> user = tppUsersController.getSelf();

        //then
        assertThat(user.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(user.getBody().getId()).isEqualTo(USER_ID);
    }

    private UserTO getUserTO(String branch) {
        return new UserTO(USER_ID, LOGIN, EMAIL, "pin", Collections.singletonList(new ScaUserDataTO()), Collections.singletonList(new AccountAccessTO()),
                          Collections.singletonList(UserRoleTO.CUSTOMER), branch);
    }

    private User getUser(String userId) {
        return new User(userId, EMAIL, LOGIN, "pin", Collections.singletonList(new ScaUserData()), Collections.singletonList(UserRole.CUSTOMER), Collections.singletonList(new AccountAccess()));
    }

    private CustomPageImpl<UserTO> getCustomPageImplUserTO() {
        return new CustomPageImpl<UserTO>();
    }
}
