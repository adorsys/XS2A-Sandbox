package de.adorsys.ledgers.oba.rest.api.domain;

import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import lombok.Value;

@Value
public class ObaAisConsent {
    private String encryptedConsent;
    private AisAccountConsent aisAccountConsent;
}
