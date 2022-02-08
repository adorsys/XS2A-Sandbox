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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.ObaPiisConsent;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.PiisConsentService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.consent.api.CmsPageInfo;
import de.adorsys.psd2.consent.api.ResponseData;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPiisClient;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AIS_BAD_REQUEST;
import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class PiisConsentServiceImpl implements PiisConsentService {
    private static final String RESPONSE_ERROR = "Error in response from CMS, please contact admin.";
    private static final String GET_CONSENTS_ERROR_MSG = "Failed to retrieve PIIS consents for user: %s, code: %s, message: %s";
    private static final String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";

    private final CmsPsuPiisClient cmsPsuPiisClient;
    private final CmsAspspPiisClient cmsAspspPiisClient;
    private final SecurityDataService securityDataService;

    @Override
    public List<ObaPiisConsent> getListOfConsents(String userLogin) {
        try {
            List<CmsPiisConsent> cmsPiisConsents = Optional.ofNullable(
                cmsPsuPiisClient.getConsentsForPsu(userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, 0, 9999).getBody())
                                                       .orElse(Collections.emptyList());
            return toObaPiisConsent(cmsPiisConsents);
        } catch (FeignException e) {
            String msg = format(GET_CONSENTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public CustomPageImpl<ObaPiisConsent> getListOfConsentsPaged(String userLogin, int page, int size) {
        try {
            ResponseData<List<CmsPiisConsent>> responseData = cmsAspspPiisClient.getConsentsForPsu(userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, page, size);
            return toCustomPage(responseData, this::toObaPiisConsent);
        } catch (FeignException e) {
            String msg = format(GET_CONSENTS_ERROR_MSG, userLogin, e.status(), e.getMessage());
            log.error(msg);
            throw ObaException.builder()
                      .devMessage(RESPONSE_ERROR)
                      .obaErrorCode(AIS_BAD_REQUEST).build();
        }
    }

    @Override
    public boolean revokeConsent(String userLogin, String consentId) {
        return Optional.ofNullable(cmsPsuPiisClient.revokeConsent(consentId, userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID).getBody())
                   .orElse(false);
    }

    private <S, R> CustomPageImpl<R> toCustomPage(ResponseData<List<S>> responseData, Function<Collection<S>, List<R>> mapper) {
        CmsPageInfo pageInfo = responseData.getPageInfo();
        int totalPages = (int) Math.ceil((double) pageInfo.getTotal() / pageInfo.getItemsPerPage());
        return new CustomPageImpl<>(
            (int) pageInfo.getPageIndex(),
            (int) pageInfo.getItemsPerPage(),
            totalPages,
            responseData.getData().size(),
            pageInfo.getTotal(),
            pageInfo.getPageIndex() > 0,
            pageInfo.getPageIndex() == 0,
            totalPages > pageInfo.getPageIndex() + 1,
            totalPages == pageInfo.getPageIndex() + 1,
            mapper.apply(responseData.getData())
        );
    }

    private List<ObaPiisConsent> toObaPiisConsent(Collection<CmsPiisConsent> aisAccountConsents) {
        return aisAccountConsents.stream()
                   .map(a -> new ObaPiisConsent(securityDataService.encryptId(a.getId()).orElse(""), a))
                   .collect(Collectors.toList());
    }
}
