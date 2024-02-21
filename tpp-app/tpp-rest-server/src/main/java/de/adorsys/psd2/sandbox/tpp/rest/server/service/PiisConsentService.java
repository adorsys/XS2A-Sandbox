/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.ConsentRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.consent.api.CmsPageInfo;
import de.adorsys.psd2.consent.api.ResponseData;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentResponse;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.ConsentStatus;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.TppPiisConsentMapper;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPiisClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PiisConsentService {
    private final CmsAspspPiisClient cmsAspspPiisClient;
    private final CmsPsuPiisClient cmsPsuPiisClient;
    private final TppPiisConsentMapper tppPiisConsentMapper;
    private final KeycloakTokenService keycloakTokenService;
    private final UserMgmtRestClient userMgmtRestClient;
    private final ConsentRestClient consentRestClient;
    private final AuthRequestInterceptor authInterceptor;

    /**
     * This method creates ASPSP PIIS consent: first, at the CMS side, then in the ledgers.
     *
     * @param piisConsent body of the consent
     * @return SCAConsentResponseTO object
     */
    public SCAConsentResponseTO createPiisConsent(String userLogin, String password, PiisConsent piisConsent) {

        BearerTokenTO token;
        try {
            token = keycloakTokenService.login(userLogin, password);
        } catch (FeignException e) {
            throw new TppException("Invalid password for user", 401);
        }

        CreatePiisConsentRequest request = tppPiisConsentMapper.toPiisConsentRequest(piisConsent);
        ResponseEntity<CreatePiisConsentResponse> response;
        try {
            response = cmsAspspPiisClient.createConsent(request, userLogin, null, null, null, null);
        } catch (FeignException e) {
            log.error(String.format("Error in creating PIIS consent in CMS, login: %s", userLogin), e);
            throw new TppException("Error while creating ASPSP PIIS consent in CMS", 400);
        }

        CreatePiisConsentResponse responseBody = response.getBody();

        if (responseBody != null) {
            String consentId = responseBody.getConsentId();
            log.info("ASPSP PIIS consent was created in CMS, its ID: " + consentId);

            UserTO user = Optional.ofNullable(userMgmtRestClient.getUser().getBody())
                              .orElseThrow(() -> new TppException("User with login: " + userLogin + " not found in Ledgers", 400));

            AisConsentTO ledgersPiisConsent = tppPiisConsentMapper.toAisConsentTO(piisConsent, user);

            authInterceptor.setAccessToken(token.getAccess_token());

            ResponseEntity<SCAConsentResponseTO> piisConsentCreationResponse;
            try {
                piisConsentCreationResponse = consentRestClient.initiatePiisConsent(ledgersPiisConsent);
            } catch (FeignException e) {
                log.error(String.format("Error in creating PIIS consent in Ledgers, login: %s", userLogin), e);
                throw new TppException("Error while creating ASPSP PIIS consent in ledgers", 400);
            } finally {
                authInterceptor.setAccessToken(null);
            }

            return piisConsentCreationResponse.getBody();

        }
        return null;
    }

    /**
     * This method returns list of ASPSP PIIS consents from CMS with pagination.
     *
     * @param userLogin PSU
     * @param page      number of page
     * @param size      size
     * @return pageable list of PIIS consents
     */
    public CustomPageImpl<PiisConsent> getListOfPiisConsentsPaged(String userLogin, int page, int size) {
        try {
            ResponseData<List<CmsPiisConsent>> responseData = cmsAspspPiisClient.getConsentsForPsu(userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, page, size);
            return toCustomPage(responseData, this::toPiisConsent);
        } catch (FeignException e) {
            log.error(String.format("Error in getting list of PIIS consents from CMS, login: %s, page: %s, size: %s", userLogin, page, size), e);
            throw new TppException("Error while getting list of ASPSP PIIS consents in CMS", 400);
        }
    }

    /**
     * This method returns ASPSP PIIS consent details by ID.
     *
     * @param consentId the identifier of PIIS consent
     * @return PIIS consent information
     */
    public PiisConsent getPiisConsent(String userLogin, String consentId) {
        try {
            Optional<PiisConsent> responseOptional = Optional.ofNullable(cmsPsuPiisClient.getConsent(consentId, userLogin, null, null, null, DEFAULT_SERVICE_INSTANCE_ID))
                                                         .map(ResponseEntity::getBody)
                                                         .map(tppPiisConsentMapper::toPiisConsent);
            return responseOptional.orElse(null);
        } catch (FeignException e) {
            log.error(String.format("Error in getting PIIS consent from CMS, login: %s", userLogin), e);
            throw new TppException(String.format("Error while getting ASPSP PIIS consent with ID: %s from CMS", consentId), 400);
        }
    }

    /**
     * Changes the given consent status to 'TERMINATED_BY_ASPSP'.
     *
     * @param consentId the identifier of PIIS consent
     */
    public void terminatePiisConsent(String consentId) {
        try {
            cmsAspspPiisClient.terminateConsent(consentId, DEFAULT_SERVICE_INSTANCE_ID);
        } catch (FeignException e) {
            throw new TppException(String.format("Error while terminating ASPSP PIIS consent with ID: %s in CMS", consentId), 400);
        }
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

    private List<PiisConsent> toPiisConsent(Collection<CmsPiisConsent> aisAccountConsents) {
        return aisAccountConsents.stream()
                   .map(a -> new PiisConsent(a.getId(), getAccountAccess(a.getAccount()), a.getTppAuthorisationNumber(), a.getExpireDate(), ConsentStatus.getByName(a.getConsentStatus().name()).orElse(null)))
                   .collect(Collectors.toList());
    }

    private AccountAccess getAccountAccess(AccountReference accountReference) {
        AccountAccess access = new AccountAccess();
        access.setIban(accountReference.getIban());
        access.setCurrency(accountReference.getCurrency());

        return access;
    }
}
