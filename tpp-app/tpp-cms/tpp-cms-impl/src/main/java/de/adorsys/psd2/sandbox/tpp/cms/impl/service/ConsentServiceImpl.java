package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CreateAisConsentResponse;
import de.adorsys.psd2.consent.service.AisConsentServiceInternal;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.cms.impl.mapper.AisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
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
                                      .map(request -> {
                                          try {
                                              return aisConsentServiceInternal.createConsent(request);
                                          } catch (WrongChecksumException e) {
                                              log.error("Could not create Consent: {}", request.getInternalRequestId());
                                              return null;
                                          }
                                      })
                                      .filter(Objects::nonNull)
                                      .map(CmsResponse::getPayload)
                                      .map(CreateAisConsentResponse::getConsentId)
                                      .collect(Collectors.toList());
        updateConsentsStatus(consentIds);
        return consentIds;
    }

    private void updateConsentsStatus(List<String> consentIds) {
        consentIds.forEach(id -> {
            try {
                aisConsentServiceInternal.updateConsentStatusById(id, ConsentStatus.VALID);
            } catch (WrongChecksumException e) {
                log.error("Could not update Consent: {} with status: {}", id, ConsentStatus.VALID.name());
            }
        });
    }
}
