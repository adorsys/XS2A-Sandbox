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

package de.adorsys.ledgers.oba.rest.server.ws.domain;

import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DecoupledContextTest {

    private static final String LOGIN = "anton.brueckner";

    @Test
    void getUndeliveredMessages_notPresentInMap() {
        // Given
        DecoupledContext context = new DecoupledContext(new DefaultSimpUserRegistry());

        // When
        List<DecoupledConfRequest> actual = context.getUndeliveredMessages(LOGIN);

        // Then
        assertTrue(actual.isEmpty());
    }

    @Test
    void getUndeliveredMessages_presentInMap() {
        // Given
        DecoupledContext context = new DecoupledContext(new DefaultSimpUserRegistry());
        context.addUndeliveredMessage(LOGIN, new DecoupledConfRequest());

        // When
        List<DecoupledConfRequest> actual = context.getUndeliveredMessages(LOGIN);

        // Then
        assertFalse(actual.isEmpty());
    }

    @Test
    void addUndeliveredMessage() {
        // Given
        DecoupledContext context = new DecoupledContext(new DefaultSimpUserRegistry());

        // When
        context.addUndeliveredMessage(LOGIN, new DecoupledConfRequest());

        // Then
        assertEquals(1, context.getUndeliveredDecoupledMessages().get(LOGIN).size());
    }

    @Test
    void clearUndeliveredMessages() {
        // Given
        DecoupledContext context = new DecoupledContext(new DefaultSimpUserRegistry());
        context.addUndeliveredMessage(LOGIN, new DecoupledConfRequest());

        // When
        context.clearUndeliveredMessages(LOGIN);

        // Then
        assertTrue(context.getUndeliveredDecoupledMessages().isEmpty());
    }

    @Test
    void checkUserIsConnected() {
        // Given
        DecoupledContext context = new DecoupledContext(new DefaultSimpUserRegistry());

        // When
        boolean actual = context.checkUserIsConnected(LOGIN);

        // Then
        assertFalse(actual);
    }
}
