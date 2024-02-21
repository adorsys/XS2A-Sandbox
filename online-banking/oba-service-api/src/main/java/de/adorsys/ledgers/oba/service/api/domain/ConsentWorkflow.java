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

package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Data
public class ConsentWorkflow {
    private final CmsAisConsentResponse consentResponse;
    private String consentStatus;
    private String authCodeMessage;
    private ConsentAuthorizeResponse authResponse;
    private final ConsentReference consentReference;
    private GlobalScaResponseTO scaResponse;

    public ConsentWorkflow(CmsAisConsentResponse consentResponse, ConsentReference consentReference) {
        if (consentResponse == null || consentReference == null) {
            throw new IllegalStateException("Do not allow null input.");
        }
        this.consentResponse = consentResponse;
        this.consentReference = consentReference;
    }

    public BearerTokenTO bearerToken() {
        return scaResponse == null
                   ? null
                   : scaResponse.getBearerToken();
    }

    public String authId() {
        return consentResponse.getAuthorisationId();
    }

    public String encryptedConsentId() {
        return consentReference.getEncryptedConsentId();
    }

    public String consentId() {
        return consentResponse.getAccountConsent().getId();
    }

    public ScaStatusTO scaStatus() {
        return scaResponse.getScaStatus();
    }

    public void storeSCAResponse(GlobalScaResponseTO consentResponse) {
        Optional.ofNullable(consentResponse)
            .ifPresent(r -> {
                if (consentResponse.getBearerToken() == null) {
                    consentResponse.setBearerToken(this.scaResponse.getBearerToken());
                }
                scaResponse = r;
                authResponse.setAuthorisationId(StringUtils.isBlank(r.getAuthorisationId()) ? authId() : r.getAuthorisationId());
                authResponse.setScaStatus(r.getScaStatus());
                authResponse.setScaMethods(r.getScaMethods());
                authResponse.setAuthConfirmationCode(r.getAuthConfirmationCode());
                authCodeMessage = r.getPsuMessage();
            });
    }
}
