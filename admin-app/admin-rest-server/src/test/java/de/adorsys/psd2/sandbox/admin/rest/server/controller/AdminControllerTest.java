/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.admin.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.*;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.ScaUserData;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.User;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.UserRole;
import de.adorsys.psd2.sandbox.admin.rest.server.mapper.UserMapper;
import de.adorsys.psd2.sandbox.cms.connector.api.service.CmsDbNativeService;
import lombok.SneakyThrows;
import org.iban4j.CountryCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.iban4j.CountryCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {
    private static final String USER_ID = "USER_ID";
    private static final String EMAIL = "EMAIL";
    private static final String LOGIN = "LOGIN";
    private static final String PASSWORD = "password";
    private static final String TPP_LOGIN = "atTpp";
    private static final String IBAN_PARAM = "IBAN param";
    private static final String ACCOUNT_ID = "ACCOUNT_ID";
    private static final String TPP_ID = "AT_666";
    private static final UserRoleTO USER_ROLE = UserRoleTO.CUSTOMER;
    private static final String COUNTRY_CODE = "AD";
    private static final List<CountryCode> COUNTRY_CODES = Arrays.asList(AD, AL, AT, BE, BG);

    @InjectMocks
    private AdminController adminController;
    @Mock
    private UserMapper userMapper;
    @Mock
    private DataRestClient dataRestClient;
    @Mock
    private AdminRestClient adminRestClient;
    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;
    @Mock
    private CmsDbNativeService cmsDbNativeService;

    @Test
    void users() {
        // Given
        UserExtendedTO userExtendedTO = getUserExtendedTO();
        CustomPageImpl<UserExtendedTO> customPage = new CustomPageImpl<>();
        customPage.setContent(List.of(userExtendedTO));
        when(adminRestClient.users(COUNTRY_CODE, TPP_ID, TPP_LOGIN, LOGIN, USER_ROLE, false, 1, 10)).thenReturn(ResponseEntity.ok(customPage));

        // When
        ResponseEntity<CustomPageImpl<UserExtendedTO>> response = adminController.users(COUNTRY_CODE, TPP_ID, TPP_LOGIN, LOGIN, USER_ROLE, false, 1, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().contains(userExtendedTO));
    }

    @Test
    void user() {
        // Given
        UserTO userTO = getUserTO();
        when(adminRestClient.user(userTO)).thenReturn(ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = adminController.user(userTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void accounts() {
        // Given
        AccountDetailsExtendedTO accountDetailsExtendedTO = getAccountDetailsExtendedTO();
        CustomPageImpl<AccountDetailsExtendedTO> customPage = new CustomPageImpl<>();
        customPage.setContent(List.of(accountDetailsExtendedTO));
        when(adminRestClient.accounts(COUNTRY_CODE, TPP_ID, TPP_LOGIN, IBAN_PARAM, false, 1, 10)).thenReturn(ResponseEntity.ok(customPage));

        // When
        ResponseEntity<CustomPageImpl<AccountDetailsExtendedTO>> response = adminController.accounts(COUNTRY_CODE, TPP_ID, TPP_LOGIN, IBAN_PARAM, false, 1, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().contains(accountDetailsExtendedTO));
    }

    @Test
    void register() {
        // Given
        UserTO userTO = getUserTO();
        when(userMapper.toUserTO(any())).thenReturn(userTO);
        when(adminRestClient.register(userTO)).thenReturn(ResponseEntity.ok(userTO));

        // When
        ResponseEntity<Void> response = adminController.register(getUser(), TPP_ID);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void admin() {
        // Given
        User user = getUser();
        UserTO userTO = getUserTO();
        when(userMapper.toUserTO(user)).thenReturn(userTO);
        when(adminRestClient.register(any())).thenReturn(ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = adminController.admin(user);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void admins() {
        // Given
        UserTO userTO = getUserTO();
        CustomPageImpl<UserTO> customPage = new CustomPageImpl<>();
        customPage.setContent(List.of(userTO));
        when(adminRestClient.admins( 1, 10)).thenReturn(ResponseEntity.ok(customPage));

        // When
        ResponseEntity<CustomPageImpl<UserTO>> response = adminController.admins(1, 10);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getContent().contains(userTO));
    }

    @Test
    void remove() {
        // Given
        when(userMgmtStaffRestClient.getBranchUserLoginsByBranchId(TPP_ID))
            .thenReturn(ResponseEntity.ok(Arrays.asList("anton.brueckner", "max.musterman")));

        when(dataRestClient.branch(any())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = adminController.remove(TPP_ID);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


//    @Test TODO: check business logic and repair test
//    @SneakyThrows
//    void removeAllTestData() {
//        // Given
//        UserExtendedTO tppExtendedTO = getStaffUserExtendedTO();
//        CustomPageImpl<UserExtendedTO> customPage = new CustomPageImpl<>();
//        customPage.setContent(List.of(tppExtendedTO));
//        when(adminRestClient.users(null, null, null, null, UserRoleTO.STAFF, null, 0, 9999)).thenReturn(ResponseEntity.ok(customPage));
//
//
//
//        when(userMgmtStaffRestClient.getBranchUserLoginsByBranchId(TPP_ID))
//            .thenReturn(ResponseEntity.ok(Arrays.asList("anton.brueckner", "max.musterman")));
//
//        when(dataRestClient.branch(any())).thenAnswer(i -> ResponseEntity.ok().build());
//
//        // When
//        ResponseEntity<Void> response = adminController.removeAllTestData();
//
//        // Then
//        assertTrue(response.getStatusCode().is2xxSuccessful());
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }


    @Test
    void updatePassword() {
        // Given
        when(adminRestClient.updatePassword(TPP_ID, PASSWORD)).thenReturn(ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = adminController.updatePassword(TPP_ID, PASSWORD);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void changeStatus() {
        // Given
        when(adminRestClient.changeStatus(TPP_ID)).thenReturn(ResponseEntity.ok(true));

        // When
        ResponseEntity<Boolean> response = adminController.changeStatus(TPP_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, LOGIN, EMAIL, "pin", Collections.singletonList(new ScaUserDataTO()), Collections.singletonList(new AccountAccessTO()),
                          Collections.singletonList(UserRoleTO.CUSTOMER), TPP_ID, false, false);
    }

    private UserExtendedTO getUserExtendedTO() {
        UserExtendedTO userExtendedTO = new UserExtendedTO();
        userExtendedTO.setBranchLogin(TPP_LOGIN);
        userExtendedTO.setBranch(TPP_ID);
        userExtendedTO.setLogin(LOGIN);
        return userExtendedTO;
    }

//    private UserExtendedTO getStaffUserExtendedTO() {
//        UserExtendedTO userExtendedTO = new UserExtendedTO();
//        userExtendedTO.setBranch(TPP_ID);
//        userExtendedTO.setId(TPP_ID);
//        userExtendedTO.setLogin(TPP_LOGIN);
//        userExtendedTO.setUserRoles(List.of(UserRoleTO.STAFF));
//        return userExtendedTO;
//    }

    private AccountDetailsExtendedTO getAccountDetailsExtendedTO() {
        AccountDetailsExtendedTO accountDetailsExtendedTO = new AccountDetailsExtendedTO();
        accountDetailsExtendedTO.setBranchLogin(TPP_LOGIN);
        return accountDetailsExtendedTO;
    }

    private User getUser() {
        return new User(USER_ID, EMAIL, LOGIN, "pin", Collections.singletonList(new ScaUserData()), Collections.singletonList(UserRole.CUSTOMER), Collections.singletonList(new AccountAccess()));
    }
}
