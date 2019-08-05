package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TppConsentService {
    private final ConsentService consentService;

    public List<String> generateConsents(List<AisConsent> consents) {
        return consentService.generateConsents(consents);
    }
}
