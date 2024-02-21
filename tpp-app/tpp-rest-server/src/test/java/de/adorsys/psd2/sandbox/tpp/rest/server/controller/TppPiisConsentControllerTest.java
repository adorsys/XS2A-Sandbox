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

import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.PiisConsentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppPiisConsentControllerTest {

    private static final String PASSWORD = "12345";
    private static final String LOGIN = "LOGIN";
    private static final String IBAN = "FR7630002005500000000000130";
    private static final String CURRENCY = "EUR";
    private static final String TPP_AUTHORISATION_NUMBER = "PSD2-FAKENCA-12345";
    private static final String PIIS_CONSENT_ID = "1234567890";

    @InjectMocks
    private TppPiisConsentController tppController;
    @Mock
    private PiisConsentService piisConsentService;

    @Test
    void createPiisConsent() {
        // Given
        PiisConsent piisConsent = getPiisConsent();
        when(piisConsentService.createPiisConsent(LOGIN, PASSWORD, piisConsent)).thenReturn(new SCAConsentResponseTO());

        // When
        ResponseEntity<SCAConsentResponseTO> actual = tppController.createPiisConsent(LOGIN, PASSWORD, piisConsent);

        // Then
        assertNotNull(actual);
        assertEquals(200, actual.getStatusCodeValue());
    }

    @Test
    void getPiisConsents() {
        // Given
        when(piisConsentService.getListOfPiisConsentsPaged(LOGIN, 0, 10)).thenReturn(new CustomPageImpl<>());

        // When
        ResponseEntity<CustomPageImpl<PiisConsent>> actual = tppController.getPiisConsents(LOGIN, 0, 10);

        // Then
        assertNotNull(actual);
        assertEquals(200, actual.getStatusCodeValue());
    }

    @Test
    void terminatePiisConsent() {
        // When
        ResponseEntity<Void> actual = tppController.terminatePiisConsent(PIIS_CONSENT_ID);

        // Then
        assertNotNull(actual);
        assertEquals(200, actual.getStatusCodeValue());
    }

    private PiisConsent getPiisConsent() {
        PiisConsent piisConsent = new PiisConsent();
        AccountAccess access = new AccountAccess();
        access.setIban(IBAN);
        access.setCurrency(Currency.getInstance(CURRENCY));
        piisConsent.setAccess(access);
        piisConsent.setTppAuthorisationNumber(TPP_AUTHORISATION_NUMBER);
        piisConsent.setValidUntil(LocalDate.now().plusMonths(1));

        return piisConsent;
    }
}
