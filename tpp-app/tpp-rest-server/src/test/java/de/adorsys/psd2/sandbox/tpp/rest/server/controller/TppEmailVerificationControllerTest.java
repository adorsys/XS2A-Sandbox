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

import de.adorsys.ledgers.middleware.client.rest.ScaVerificationRestClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppEmailVerificationControllerTest {
    private static final String EMAIL = "EMAIL";
    private static final String TOKEN = "TOKEN";

    @InjectMocks
    private TppEmailVerificationController verificationController;
    @Mock
    private ScaVerificationRestClient scaVerificationRestClient;

    @Test
    void sendEmailVerification() {
        // Given
        when(scaVerificationRestClient.sendEmailVerification(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = verificationController.sendEmailVerification(EMAIL);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void confirmVerificationToken() {
        // Given
        when(scaVerificationRestClient.confirmVerificationToken(anyString())).thenAnswer(i -> ResponseEntity.ok().build());

        // When
        ResponseEntity<Void> response = verificationController.confirmVerificationToken(TOKEN);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }
}
