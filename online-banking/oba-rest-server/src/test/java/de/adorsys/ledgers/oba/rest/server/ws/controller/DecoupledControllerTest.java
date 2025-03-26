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

package de.adorsys.ledgers.oba.rest.server.ws.controller;

import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.ws.domain.DecoupledContext;
import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import de.adorsys.ledgers.oba.service.api.service.DecoupledService;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DecoupledControllerTest {

    private static final String TOKEN = "token";
    private static final String LOGIN = "anton.brueckner";

    @InjectMocks
    private DecoupledController decoupledController;
    @Mock
    private SimpMessagingTemplate template;
    @Mock
    private DecoupledContext context;
    @Mock
    private MiddlewareAuthentication authentication;
    @Mock
    private DecoupledService decoupledService;

    @Test
    void decoupled() {
        // Given
        BearerTokenTO token = new BearerTokenTO();
        token.setAccess_token(TOKEN);
        when(authentication.getBearerToken()).thenReturn(token);
        when(decoupledService.executeDecoupledOpr(any(DecoupledConfRequest.class), anyString())).thenReturn(true);
        // When
        ResponseEntity<Boolean> actual = decoupledController.decoupled(getDecoupledRequest(OpTypeTO.PAYMENT));

        // Then
        assertNotNull(actual);
        assertEquals(Boolean.TRUE, actual.getBody());
    }

    @Test
    void sendNotification_userNotConnected() {
        // Given
        BearerTokenTO token = new BearerTokenTO();
        token.setAccess_token(TOKEN);

        when(context.checkUserIsConnected(LOGIN)).thenReturn(false);
        // When
        ResponseEntity<Boolean> actual = decoupledController.sendNotification(getDecoupledRequest(OpTypeTO.CONSENT));

        // Then
        assertNotNull(actual);
        verify(context, times(1)).addUndeliveredMessage(any(), any());
        assertEquals(Boolean.TRUE, actual.getBody());
    }

    @Test
    void sendNotification_userConnected() {
        // Given
        BearerTokenTO token = new BearerTokenTO();
        token.setAccess_token(TOKEN);

        when(context.checkUserIsConnected(LOGIN)).thenReturn(true);
        // When
        ResponseEntity<Boolean> actual = decoupledController.sendNotification(getDecoupledRequest(OpTypeTO.CONSENT));

        // Then
        assertNotNull(actual);
        verify(template, times(1)).convertAndSendToUser(any(), any(), any());
        assertEquals(Boolean.TRUE, actual.getBody());
    }

    private DecoupledConfRequest getDecoupledRequest(OpTypeTO opType) {
        DecoupledConfRequest request = new DecoupledConfRequest();
        request.setConfirmed(true);
        request.setObjId("objId");
        request.setAuthCode("TAN");
        request.setOpType(opType);
        request.setAddressedUser("anton.brueckner");
        return request;
    }
}
