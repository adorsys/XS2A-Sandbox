package de.adorsys.psd2.sandbox.tpp.rest.server.controller;


import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppConsentControllerTest {
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
    public void generateConsents() throws IOException {
        //given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.of(Collections.singletonList(getAisConsent())));
        when(tppConsentService.generateConsents(any())).thenReturn(Collections.singletonList("consent"));

        //when
        ResponseEntity<List<String>> response = tppConsentController.generateConsents(resolveMultipartFile(FILE_NAME));

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertFalse(Objects.requireNonNull(response.getBody()).isEmpty());
    }

    @Test(expected = TppException.class)
    public void generateConsents_couldNotParseData() throws IOException {
        //given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.empty());

        //when
        tppConsentController.generateConsents(resolveMultipartFile(FILE_NAME));
    }

    @Test
    public void downloadConsentTemplate() {
        //given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(resourceLoader.getResource(FILE_NAME));

        //when
        ResponseEntity<Resource> response = tppConsentController.downloadConsentTemplate();

        //then
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
