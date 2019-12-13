package de.adorsys.ledgers.oba.service.api.domain;

import lombok.Data;

@Data
public class ConsentReference {
    private String authorizationId;
    private String redirectId;
    private ConsentType consentType;
    private String encryptedConsentId;
    private String cookieString;
}
