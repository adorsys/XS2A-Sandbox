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
import de.adorsys.ledgers.oba.service.api.service.ConsentReferencePolicy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DefaultConsentReferencePolicy implements ConsentReferencePolicy {


    @Override
    public ConsentReference fromURL(String redirectId, ConsentType consentType, String encryptedConsentId) {
        ConsentReference cr = new ConsentReference();
        cr.setRedirectId(redirectId);
        cr.setConsentType(consentType);
        cr.setEncryptedConsentId(encryptedConsentId);
        return cr;
    }

    @Override
    public ConsentReference fromRequest(String encryptedConsentId, String authorizationId) {
        return consentReference(encryptedConsentId, authorizationId, ConsentType.AIS, authorizationId);
    }


    private ConsentReference consentReference(String encryptedConsentId, String authorizationId, ConsentType type, String redirectId) {
        ConsentReference cr = new ConsentReference();
        cr.setConsentType(type);
        cr.setRedirectId(redirectId);
        cr.setEncryptedConsentId(encryptedConsentId);
        cr.setAuthorizationId(authorizationId);
        return cr;
    }


}
