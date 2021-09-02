package de.adorsys.psd2.sandbox.cms.connector.api.service;

import de.adorsys.psd2.sandbox.cms.connector.api.domain.AisConsent;

import java.util.List;

public interface ConsentService {
    default List<String> generateConsents(List<AisConsent> consents) {
        throw new UnsupportedOperationException("Should be implemented");
    }
}
