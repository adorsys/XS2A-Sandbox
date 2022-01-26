/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

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
