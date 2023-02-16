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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;


import de.adorsys.psd2.sandbox.cms.connector.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.cms.connector.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppConsentControllerTest {
    private static final String FILE_NAME = "consents_template.yml";
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @InjectMocks
    private TppConsentController tppConsentController;
    @Mock
    private ConsentService tppConsentService;
    @Mock
    private DownloadResourceService downloadResourceService;
    @Mock
    private ParseService parseService;

    @Test
    void generateConsents() throws IOException {
        // Given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.of(Collections.singletonList(getAisConsent())));
        when(tppConsentService.generateConsents(any())).thenReturn(Collections.singletonList("consent"));

        // When
        ResponseEntity<List<String>> response = tppConsentController.generateConsents(resolveMultipartFile(FILE_NAME));

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test
    void generateConsents_couldNotParseData() throws IOException {
        // Given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.empty());
        MultipartFile file = resolveMultipartFile(FILE_NAME);
        // When
        assertThrows(TppException.class, () -> tppConsentController.generateConsents(file));
    }

    @Test
    void downloadConsentTemplate() {
        // Given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(resourceLoader.getResource(FILE_NAME));

        // When
        ResponseEntity<Resource> response = tppConsentController.downloadConsentTemplate();

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    private AisConsent getAisConsent() {
        AisConsent consent = new AisConsent();
        consent.setTppRedirectPreferred(false);
        return consent;
    }

    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }
}
