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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DownloadResourceServiceTest {
    private static final String FILE_NAME = "consents_template.yml";
    private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @InjectMocks
    private DownloadResourceService downloadResourceService;
    @Mock
    private ResourceLoader resourceLoader;

    @Test
    void getResourceByTemplate() {
        // Given
        when(resourceLoader.getResource(any())).thenReturn(getResource());

        // When
        Resource resource = downloadResourceService.getResourceByTemplate(FILE_NAME);

        // Then
        assertEquals(FILE_NAME, resource.getFilename());
        assertTrue(resource.exists());
    }

    private Resource getResource() {
        return defaultResourceLoader.getResource(FILE_NAME);
    }
}
