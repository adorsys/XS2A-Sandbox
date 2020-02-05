package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DownloadResourceServiceTest {
    private static final String FILE_NAME = "consents_template.yml";
    private final ResourceLoader defaultResourceLoader = new DefaultResourceLoader();

    @InjectMocks
    private DownloadResourceService downloadResourceService;
    @Mock
    private ResourceLoader resourceLoader;

    @Test
    public void getResourceByTemplate() {
        //given
        when(resourceLoader.getResource(any())).thenReturn(getResource());

        //when
        Resource resource = downloadResourceService.getResourceByTemplate(FILE_NAME);

        //then
        assertEquals(FILE_NAME, resource.getFilename());
        assertTrue(resource.exists());
    }

    private Resource getResource() {
        return defaultResourceLoader.getResource(FILE_NAME);
    }
}
