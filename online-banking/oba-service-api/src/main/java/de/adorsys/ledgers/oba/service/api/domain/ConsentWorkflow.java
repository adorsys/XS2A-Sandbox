package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import lombok.Data;

import java.util.Optional;

@Data
public class ConsentWorkflow {
    private final CmsAisConsentResponse consentResponse;
    private String consentStatus;
    private String authCodeMessage;
    private ConsentAuthorizeResponse authResponse;
    private final ConsentReference consentReference;
    private SCAResponseTO scaResponse;

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

    public void storeSCAResponse(SCAResponseTO consentResponse) {
        Optional.ofNullable(consentResponse)
            .ifPresent(r -> {
                scaResponse = r;
                authResponse.setAuthorisationId(r.getAuthorisationId());
                authResponse.setScaStatus(r.getScaStatus());
                authResponse.setScaMethods(r.getScaMethods());
                authCodeMessage = r.getPsuMessage();
            });
    }
}
