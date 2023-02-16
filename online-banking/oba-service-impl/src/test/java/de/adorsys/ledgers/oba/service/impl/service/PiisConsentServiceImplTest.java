/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.consent.api.CmsPageInfo;
import de.adorsys.psd2.consent.api.ResponseData;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.consent.service.security.SecurityDataService;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import feign.FeignException;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPiisClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AIS_BAD_REQUEST;
import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PiisConsentServiceImplTest {

    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    private static final String IBAN = "DE1234567890";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final String USER_LOGIN = "login";

    @InjectMocks
    private PiisConsentServiceImpl consentService;

    @Mock
    private CmsPsuPiisClient cmsPsuPiisClient;
    @Mock
    private SecurityDataService securityDataService;
    @Mock
    private CmsAspspPiisClient cmsAspspPiisClient;

    @Test
    void getListOfConsents() {
        // Given
        CmsPiisConsent cmsPiisConsent = getCmsPiisConsent();
        when(cmsPsuPiisClient.getConsentsForPsu(any(), any(), any(), any(), any(), any(), any())).thenReturn(ResponseEntity.ok(Collections.singletonList(cmsPiisConsent)));
        when(securityDataService.encryptId(any())).thenReturn(Optional.of("consent"));

        // When
        List<ObaPiisConsent> listOfConsents = consentService.getListOfConsents(USER_LOGIN);

        // Then
        assertNotNull(listOfConsents);
        assertEquals("consent", listOfConsents.get(0).getEncryptedConsent());
        assertThat(listOfConsents.get(0).getCmsPiisConsent()).isEqualTo(cmsPiisConsent);
    }

    @Test
    void getListOfConsents_failedGetConsent() {
        // Given
        when(cmsPsuPiisClient.getConsentsForPsu(any(), any(), any(), any(), any(), any(), any())).thenThrow(FeignException.class);

        // Then
        assertThrows(ObaException.class, () -> consentService.getListOfConsents(USER_LOGIN));
    }

    @Test
    void revokeConsentSuccess() {
        // Given
        when(cmsPsuPiisClient.revokeConsent(CONSENT_ID, USER_LOGIN, null, null, null, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(ResponseEntity.ok(Boolean.TRUE));

        // Then
        assertTrue(consentService.revokeConsent(USER_LOGIN, CONSENT_ID));
    }

    private CmsPiisConsent getCmsPiisConsent() {
        return new CmsPiisConsent(CONSENT_ID, false, java.time.OffsetDateTime.now(), LocalDate.now().minusDays(1), LocalDate.now().plusMonths(1), new PsuIdData(), de.adorsys.psd2.xs2a.core.consent.ConsentStatus.VALID, getReference(), java.time.OffsetDateTime.now(), "", "cardNumber", LocalDate.now().plusMonths(9), "cardInformation", "registrationInformation", java.time.OffsetDateTime.now().minusDays(1), "tppAutthNumber");
    }

    private AccountReference getReference() {
        AccountReference reference = new AccountReference();
        reference.setIban(IBAN);
        reference.setCurrency(EUR);
        return reference;
    }

    @Test
    void getListOfConsentsPaged() {
        // Given
        CmsPiisConsent consent = new CmsPiisConsent();
        List<CmsPiisConsent> collection = IntStream.range(0, 10)
                                              .mapToObj(i -> {
                                                  consent.setId(String.valueOf(i));
                                                  return consent;
                                              }).collect(Collectors.toList());
        ResponseData<List<CmsPiisConsent>> response = new ResponseData<>();
        response.setData(collection);
        response.setPageInfo(new CmsPageInfo(0, 10, 10));
        when(cmsAspspPiisClient.getConsentsForPsu(any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(response);

        // When
        CustomPageImpl<ObaPiisConsent> result = consentService.getListOfConsentsPaged(USER_LOGIN, 0, 10);

        // Then
        assertEquals(10, result.getNumberOfElements());
        assertEquals(0, result.getNumber());
        assertTrue(result.isFirstPage());
        assertTrue(result.isFirstPage());
        assertFalse(result.isNextPage());
        assertTrue(result.isLastPage());
        assertEquals(10, result.getTotalElements());
        assertEquals(10, result.getContent().size());
    }

    @Test
    void getListOfConsentsPaged_error() {
        when(cmsAspspPiisClient.getConsentsForPsu(any(), any(), any(), any(), any(), any(), any()))
            .thenThrow(FeignException.class);
        ObaException exception = assertThrows(ObaException.class, () -> consentService.getListOfConsentsPaged(USER_LOGIN, 0, 10));
        assertEquals(AIS_BAD_REQUEST, exception.getObaErrorCode());
    }
}
