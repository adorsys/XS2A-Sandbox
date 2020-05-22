package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserRole;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppAdminControllerTest {

    @InjectMocks
    private TppAdminController controller;

    @Mock
    private UserMapper userMapper;
    @Mock
    private DataRestClient dataRestClient;
    @Mock
    private AdminRestClient adminRestClient;

    @Test
    void users() {
        when(adminRestClient.users(any(), any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<CustomPageImpl<UserExtendedTO>> result = controller.users("", "", "", "", UserRoleTO.CUSTOMER, false, 0, 1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void user() {
        when(adminRestClient.user(any())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = controller.user(new UserTO());
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void accounts() {
        when(adminRestClient.accounts(any(), any(), any(), any(), any(), anyInt(), anyInt())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<CustomPageImpl<AccountDetailsExtendedTO>> result = controller.accounts("", "", "", "", false, 0, 1);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void register() {
        when(adminRestClient.register(any())).thenReturn(ResponseEntity.created(URI.create("")).build());
        when(userMapper.toUserTO(any())).thenAnswer(a -> Mappers.getMapper(UserMapper.class).toUserTO(a.getArgument(0)));
        User request = new User();
        request.setUserRoles(Collections.singletonList(UserRole.CUSTOMER));
        ResponseEntity<Void> result = controller.register(request, "TPP");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void remove() {
        when(dataRestClient.branch(anyString())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = controller.remove("TPP");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void updatePassword() {
        when(adminRestClient.updatePassword(anyString(), anyString())).thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = controller.updatePassword("TPP", "pin");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void changeStatus() {
        when(adminRestClient.changeStatus(anyString())).thenReturn(ResponseEntity.ok(true));
        ResponseEntity<Boolean> result = controller.changeStatus("TPP");
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody());
    }
}
