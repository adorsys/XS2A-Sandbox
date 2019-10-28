package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.consent.api.ais.CreateAisConsentResponse;
import de.adorsys.psd2.consent.service.AisConsentServiceInternal;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.cms.impl.mapper.AisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsentServiceImpl implements ConsentService {
    private final AisConsentServiceInternal aisConsentServiceInternal;
    private final AisConsentMapper aisConsentMapper;

    @Override
    @Transactional
    public List<String> generateConsents(List<AisConsent> consents) {
        List<String> consentIds = consents.stream()
                                      .map(aisConsentMapper::toCmsAisConsentRequest)
                                      .map(aisConsentServiceInternal::createConsent)
                                      .map(Optional::get)
                                      .map(CreateAisConsentResponse::getConsentId)
                                      .collect(Collectors.toList());
        updateConsentsStatus(consentIds);
        return consentIds;
    }

    private void updateConsentsStatus(List<String> consentIds) {
        consentIds.forEach(id -> aisConsentServiceInternal.updateConsentStatusById(id, ConsentStatus.VALID));
    }
}
