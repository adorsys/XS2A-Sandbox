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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppUsersControllerTest {
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
    void createUser() {
        // Given
        when(userMapper.toUserTO(any())).thenReturn(getUserTO(BRANCH));
        when(userMgmtStaffRestClient.createUser(any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        // When
        ResponseEntity<UserTO> user = tppUsersController.createUser(getUser(USER_ID));

        // Then
        assertTrue(user.getStatusCode().is2xxSuccessful());
        assertThat(Objects.requireNonNull(user.getBody()).getId()).isEqualTo(getUserTO(BRANCH).getId());
    }

    @Test
    void getAllUsers() {
        // Given
        when(userMgmtStaffRestClient.getBranchUsersByRoles(Collections.singletonList(USER_ROLE), LOGIN, null, 0, 25)).thenReturn(ResponseEntity.ok(getCustomPageImplUserTO()));

        // When
        ResponseEntity<CustomPageImpl<UserTO>> user = tppUsersController.getAllUsers(LOGIN, 0, 25);

        // Then
        assertTrue(user.getStatusCode().is2xxSuccessful());
        assertEquals(user.getBody(), getCustomPageImplUserTO());
    }

    @Test
    void updateUser() {
        // Given
        when(userMapper.toUserTO(any())).thenReturn(getUserTO(BRANCH));
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));
        when(userMgmtStaffRestClient.modifyUser(any(), any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        // When
        ResponseEntity<Void> user = tppUsersController.updateUser(getUser(USER_ID));

        // Then
        assertTrue(user.getStatusCode().is2xxSuccessful());
    }

    @Test
    void updateUser_userIdNull() {
        User user = getUser(null);
        // Then
        assertThrows(TppException.class, () -> tppUsersController.updateUser(user));
    }

    @Test
    void updateUser_tppCodedNull() {
        // Given
        UserTO userTO = getUserTO(null);
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(userTO));
        User user = getUser(USER_ID);
        // Then
        assertThrows(TppException.class, () -> tppUsersController.updateUser(user));
    }

    @Test
    void getUser() {
        // Given
        when(userMgmtStaffRestClient.getBranchUserById(any())).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        // When
        ResponseEntity<UserTO> user = tppUsersController.getUser(USER_ID);

        // Then
        assertTrue(user.getStatusCode().is2xxSuccessful());
        assertEquals(USER_ID, user.getBody().getId());
    }

    @Test
    void getSelf() {
        // Given
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUserTO(BRANCH)));

        // When
        ResponseEntity<UserTO> user = tppUsersController.getSelf();

        // Then
        assertTrue(user.getStatusCode().is2xxSuccessful());
        assertEquals(USER_ID, user.getBody().getId());
    }

    @Test
    void changeStatus() {
        when(userMgmtStaffRestClient.changeStatus(anyString())).thenReturn(ResponseEntity.ok(true));
        ResponseEntity<Boolean> result = tppUsersController.changeStatus(USER_ID);
        assertEquals(ResponseEntity.ok(true), result);
    }

    private UserTO getUserTO(String branch) {
        return new UserTO(USER_ID, LOGIN, EMAIL, "pin", Collections.singletonList(new ScaUserDataTO()), Collections.singletonList(new AccountAccessTO()),
                          Collections.singletonList(UserRoleTO.CUSTOMER), branch, false, false);
    }

    private User getUser(String userId) {
        return new User(userId, EMAIL, LOGIN, "pin", Collections.singletonList(new ScaUserData()), Collections.singletonList(UserRole.CUSTOMER), Collections.singletonList(new AccountAccess()));
    }

    private CustomPageImpl<UserTO> getCustomPageImplUserTO() {
        CustomPageImpl<UserTO> users = new CustomPageImpl<>();
        users.setContent(Collections.singletonList(new UserTO()));
        return users;
    }
}
