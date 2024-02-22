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

package de.adorsys.psd2.sandbox.cms.connector.impl.service;

import de.adorsys.psd2.consent.api.CmsResponse;
import de.adorsys.psd2.consent.api.WrongChecksumException;
import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.consent.api.consent.CmsCreateConsentResponse;
import de.adorsys.psd2.consent.service.ConsentServiceInternal;
import de.adorsys.psd2.sandbox.cms.connector.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.cms.connector.impl.mapper.ConsentMapper;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsentServiceImplTest {

    private static final String CONSENT_ID = "13456789";
    @InjectMocks
    private ConsentServiceImpl service;

    @Mock
    private ConsentServiceInternal consentServiceInternal;
    @Mock
    private ConsentMapper mapper;

    @Test
    void generateConsents() throws WrongChecksumException {
        // Given
        when(mapper.mapToCmsConsent(any())).thenReturn(new CmsConsent());
        when(consentServiceInternal.createConsent(any())).thenReturn(getCmsResponse());

        // When
        List<String> result = service.generateConsents(Collections.singletonList(consent()));

        // Then
        assertThat(result).isEqualTo(Collections.singletonList(CONSENT_ID));
    }

    private CmsResponse<CmsCreateConsentResponse> getCmsResponse() {
        CmsConsent consent = new CmsConsent();
        consent.setConsentStatus(ConsentStatus.VALID);
        return CmsResponse.<CmsCreateConsentResponse>builder()
                   .payload(new CmsCreateConsentResponse(CONSENT_ID, consent))
                   .build();
    }

    private AisConsent consent() {
        return new AisConsent();
    }
}
