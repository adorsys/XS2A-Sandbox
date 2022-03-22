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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
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
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.TppPiisConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.List;

import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PiisConsentServiceTest {

    @InjectMocks
    private PiisConsentService piisConsentService;
    @Mock
    private TppPiisConsentMapper tppPiisConsentMapper;
    @Mock
    private CmsAspspPiisClient cmsAspspPiisClient;
    @Mock
    private KeycloakTokenService keycloakTokenService;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;
    @Mock
    private AuthRequestInterceptor authInterceptor;
    @Mock
    private ConsentRestClient consentRestClient;

    private static final String PASSWORD = "12345";
    private static final String LOGIN = "anton.brueckner";
    private static final String EMAIL = "anton.brueckner@mail.de";
    private static final String BRANCH_ID = "branch_id";
    private static final String IBAN = "FR7630002005500000000000130";
    private static final String USER_ID = "gec68568dscv";
    private static final String CURRENCY = "EUR";
    private static final String TPP_AUTHORISATION_NUMBER = "PSD2-FAKENCA-12345";
    private static final String CONSENT_ID = "74657642756964275674267956425";
    private static final int TOKEN_EXPIRES_IN = 600;

    @Test
    void createPiisConsent() {
        // Given
        CreatePiisConsentRequest request = new CreatePiisConsentRequest();
        request.setTppAuthorisationNumber(TPP_AUTHORISATION_NUMBER);

        PiisConsent piisConsent = getPiisConsent();

        when(tppPiisConsentMapper.toPiisConsentRequest(piisConsent))
            .thenReturn(request);
        when(cmsAspspPiisClient.createConsent(request, LOGIN, null, null, null, null))
            .thenReturn(ResponseEntity.ok(getPiisConsentResponse()));
        when(keycloakTokenService.login(LOGIN, PASSWORD))
            .thenReturn(getBearerToken());

        UserTO user = getUserTO();

        when(userMgmtRestClient.getUser())
            .thenReturn(ResponseEntity.ok(user));
        when(tppPiisConsentMapper.toAisConsentTO(piisConsent, user))
            .thenReturn(getAisConsentTO());
        when(consentRestClient.initiatePiisConsent(getAisConsentTO()))
            .thenReturn(ResponseEntity.ok(getSCAConsentResponseTO()));

        // When
        SCAConsentResponseTO actual = piisConsentService.createPiisConsent(LOGIN, PASSWORD, piisConsent);

        // Then
        assertNotNull(actual);
        assertEquals(CONSENT_ID, actual.getConsentId());
    }

    @Test
    void getListOfPiisConsentsPaged() {
        // Given
        ResponseData<List<CmsPiisConsent>> response = new ResponseData<>();
        response.setData(Collections.singletonList(getCmsPiisConsent()));
        response.setPageInfo(new CmsPageInfo(0, 10, 10));

        when(cmsAspspPiisClient.getConsentsForPsu(LOGIN, null, null, null, DEFAULT_SERVICE_INSTANCE_ID, 0, 10))
            .thenReturn(response);

        // When
        CustomPageImpl<PiisConsent> consentsPaged = piisConsentService.getListOfPiisConsentsPaged(LOGIN, 0, 10);

        // Then
        assertNotNull(consentsPaged.getContent());

    }

    private PiisConsent getPiisConsent() {
        PiisConsent piisConsent = new PiisConsent();
        AccountAccess access = new AccountAccess();
        access.setIban(IBAN);
        access.setCurrency(Currency.getInstance(CURRENCY));
        piisConsent.setAccess(access);
        piisConsent.setTppAuthorisationNumber(TPP_AUTHORISATION_NUMBER);
        piisConsent.setValidUntil(LocalDate.now().plusMonths(1));

        return piisConsent;
    }

    private CmsPiisConsent getCmsPiisConsent() {
        CmsPiisConsent piisConsent = new CmsPiisConsent();
        AccountAccess access = new AccountAccess();
        access.setIban(IBAN);
        access.setCurrency(Currency.getInstance(CURRENCY));
        piisConsent.setAccount(new AccountReference());
        piisConsent.setTppAuthorisationNumber(TPP_AUTHORISATION_NUMBER);
        piisConsent.setExpireDate(LocalDate.now().plusMonths(1));
        piisConsent.setConsentStatus(ConsentStatus.VALID);

        return piisConsent;
    }

    private CreatePiisConsentResponse getPiisConsentResponse() {
        return new CreatePiisConsentResponse(CONSENT_ID);
    }

    private BearerTokenTO getBearerToken() {
        AccessTokenTO token = new AccessTokenTO();
        token.setLogin(LOGIN);
        return new BearerTokenTO(null, null, TOKEN_EXPIRES_IN, null, token, new HashSet<>());
    }

    private UserTO getUserTO() {
        return new UserTO(USER_ID, LOGIN, EMAIL, PASSWORD, null, null, null, BRANCH_ID, false, false);
    }

    private AisConsentTO getAisConsentTO() {
        return new AisConsentTO(CONSENT_ID, LOGIN, TPP_AUTHORISATION_NUMBER, 5, null, LocalDate.now().plusMonths(1), false);
    }

    private SCAConsentResponseTO getSCAConsentResponseTO() {
        SCAConsentResponseTO response = new SCAConsentResponseTO();
        response.setConsentId(CONSENT_ID);

        return response;
    }

}
