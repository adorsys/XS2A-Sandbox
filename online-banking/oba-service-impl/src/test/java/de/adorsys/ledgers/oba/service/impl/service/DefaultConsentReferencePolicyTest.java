
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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultConsentReferencePolicyTest {
    private static final String AUTHORIZATION_ID = "authorizationID";
    private static final ConsentType CONSENT_TYPE_AIS = ConsentType.AIS;
    private static final String ENCRYPTED_CONSENT_ID = "encryptedConsentId";
    private static final String REDIRECT_ID = "redirectID";

    @InjectMocks
    private DefaultConsentReferencePolicy defaultConsentReferencePolicy;

    @Test
    void fromURL() throws NoSuchFieldException {
        // When
        ConsentReference reference = defaultConsentReferencePolicy.fromURL(REDIRECT_ID, CONSENT_TYPE_AIS, ENCRYPTED_CONSENT_ID);

        // Then
        assertNotNull(reference);
        assertEquals(REDIRECT_ID, reference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, reference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, reference.getEncryptedConsentId());
    }

    @Test
    void fromRequest() throws NoSuchFieldException {
        // When
        ConsentReference consentReference = defaultConsentReferencePolicy.fromRequest(ENCRYPTED_CONSENT_ID, AUTHORIZATION_ID);

        // Then
        assertNotNull(consentReference);
        assertEquals(AUTHORIZATION_ID, consentReference.getAuthorizationId());
        assertEquals(AUTHORIZATION_ID, consentReference.getRedirectId());
        assertEquals(CONSENT_TYPE_AIS, consentReference.getConsentType());
        assertEquals(ENCRYPTED_CONSENT_ID, consentReference.getEncryptedConsentId());
    }

}
