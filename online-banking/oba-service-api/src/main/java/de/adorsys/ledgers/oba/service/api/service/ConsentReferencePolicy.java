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

package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.ConsentReference;
import de.adorsys.ledgers.oba.service.api.domain.ConsentType;

/**
 * Defines how to reference a consent.
 *
 * @author fpo
 */
public interface ConsentReferencePolicy {

    /**
     * Produces The consent reference based on the inbound URL parameter. I sure, just store those url parameters
     * so we can keep them in a consent cookie.
     * <p>
     * The inbound url will contain either an ais consentId or the pis paymentId. We call any of them an encrypted consent id.
     *
     * @param redirectId:        the redirect id.
     * @param consentType:       the type of consent requested payment, account information, cancelation.
     * @param encryptedConsentId the encrypted consent id
     * @return a holder of the consent references
     */
    ConsentReference fromURL(String redirectId, ConsentType consentType, String encryptedConsentId);

    /**
     * Produces a consent reference from a web request. Given encryptedConsentId and authorizationId are matched
     * against the values stored in the cookie.
     *
     * @param encryptedConsentId : the encrypted consent id from the request url
     * @param authorizationId    : the authorization id from the request url
     * @return a holder of the consent references
     */
    ConsentReference fromRequest(String encryptedConsentId, String authorizationId);
}
