/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.ObaPiisConsent;
import de.adorsys.ledgers.oba.service.api.service.PiisConsentService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObaPiisConsentControllerTest {

    private static final String LOGIN = "login";
    private static final String PIIS_CONSENT_ID = "consentId";

    @InjectMocks
    private ObaPiisConsentController controller;

    @Mock
    private PiisConsentService piisConsentService;

    @Test
    void getPiisConsents() {
        // Given
        when(piisConsentService.getListOfConsents(anyString())).thenReturn(getObaPiisConsents());

        // When
        ResponseEntity<List<ObaPiisConsent>> response = controller.getPiisConsents(LOGIN);

        // Then
        assertEquals(ResponseEntity.ok(getObaPiisConsents()), response);
    }

    @Test
    void getPiisConsentsPaged() {
        // Given
        CustomPageImpl<ObaPiisConsent> customPaged = new CustomPageImpl<>(1, 1, 1, 1, 1, false, true, true, true, getObaPiisConsents());
        when(piisConsentService.getListOfConsentsPaged(anyString(), anyInt(), anyInt())).thenReturn(customPaged);

        // When
        ResponseEntity<CustomPageImpl<ObaPiisConsent>> response = controller.getPiisConsentsPaged(LOGIN, 1, 1);

        // Then
        assertEquals(ResponseEntity.ok(customPaged), response);
    }

    @Test
    void revokeConsent() {
        // Given
        when(piisConsentService.revokeConsent(anyString(), anyString())).thenReturn(true);

        // When
        ResponseEntity<Boolean> response = controller.revokeConsent(LOGIN, PIIS_CONSENT_ID);

        // Then
        assertEquals(ResponseEntity.ok(true), response);
    }

    private List<ObaPiisConsent> getObaPiisConsents() {
        return Collections.singletonList(new ObaPiisConsent(PIIS_CONSENT_ID, new CmsPiisConsent()));
    }

}
