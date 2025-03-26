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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppRecoveryPointControllerTest {

    @InjectMocks
    private TppRecoveryPointController tppRecoveryPointController;
    @Mock
    private DataRestClient dataRestClient;

    @Test
    void point() {
        // Given
        when(dataRestClient.getPoint(any(Long.class))).thenReturn(ResponseEntity.ok(new RecoveryPointTO()));

        // When
        ResponseEntity<RecoveryPointTO> actual = tppRecoveryPointController.point(1L);

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }

    @Test
    void points() {
        // Given
        when(dataRestClient.getAllPoints()).thenReturn(ResponseEntity.ok(Collections.singletonList(new RecoveryPointTO())));

        // When
        ResponseEntity<List<RecoveryPointTO>> actual = tppRecoveryPointController.points();

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }

    @Test
    void createPoint() {
        // Given
        when(dataRestClient.createPoint(any(RecoveryPointTO.class))).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));

        // When
        ResponseEntity<Void> actual = tppRecoveryPointController.createPoint(new RecoveryPointTO());

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deletePoint() {
        // Given
        when(dataRestClient.deletePoint(any(Long.class))).thenReturn(new ResponseEntity<>(HttpStatusCode.valueOf(200)));

        // When
        ResponseEntity<Void> actual = tppRecoveryPointController.deletePoint(1L);

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }
}
