package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.consent.service.AisConsentServiceInternal;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.cms.impl.mapper.AisConsentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {
    private final AisConsentServiceInternal aisConsentServiceInternal;
    private final AisConsentMapper aisConsentMapper;

    @Override
    public List<String> generateConsents(List<AisConsent> consents) {
        return consents.stream()
                   .map(aisConsentMapper::toCmsAisConsentRequest)
                   .map(aisConsentServiceInternal::createConsent)
                   .map(Optional::get)
                   .collect(Collectors.toList());
    }
}
