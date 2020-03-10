package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.consent.CmsCreateConsentResponse;
import de.adorsys.psd2.consent.service.ConsentServiceInternal;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.cms.impl.mapper.ConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    private final ConsentServiceInternal consentServiceInternal;
    private final ConsentMapper mapper;

    @Override
    @Transactional
    public List<String> generateConsents(List<AisConsent> consents) {
        List<String> consentIds = consents.stream()
                                      .map(mapper::mapToCmsConsent)
                                      .map(this::doCreateConsent)
                                      .filter(Objects::nonNull)
                                      .map(CmsResponse::getPayload)
                                      .map(CmsCreateConsentResponse::getConsentId)
                                      .collect(Collectors.toList());
        return updateConsentsStatus(consentIds);
    }

    @SneakyThrows
    private CmsResponse<CmsCreateConsentResponse> doCreateConsent(CmsConsent cmsConsent) {
        return consentServiceInternal.createConsent(cmsConsent);
    }

    private List<String> updateConsentsStatus(List<String> consentIds) {
        consentIds.forEach(id -> {
            try {
                consentServiceInternal.updateConsentStatusById(id, ConsentStatus.VALID);
            } catch (WrongChecksumException e) {
                log.error("Could not update Consent: {} with status: {}", id, ConsentStatus.VALID.name());
            }
        });
        return consentIds;
    }
}
