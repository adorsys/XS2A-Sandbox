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
import static org.mockito.Matchers.any;
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
