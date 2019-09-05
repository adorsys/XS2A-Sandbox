package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.DownloadResource;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppConsentRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TppConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(TppConsentRestApi.BASE_PATH)
@RequiredArgsConstructor
public class TppConsentController implements TppConsentRestApi {
    private static final String CONSENT_TEMPLATE = "classpath:consents_template.yml";

    private final TppConsentService tppConsentService;
    private final DownloadResourceService downloadResourceService;
    private final ParseService parseService;

    @Override
    public ResponseEntity<List<String>> generateConsents(MultipartFile file) {
        log.info("Update file received");
        List<AisConsent> parsed = parseService.getDataFromFile(file, new TypeReference<List<AisConsent>>() {
        }).orElseThrow(() -> new TppException("Could not parse data", 400));
        return ResponseEntity.ok(tppConsentService.generateConsents(parsed));
    }

    @Override
    public ResponseEntity<Resource> downloadConsentTemplate() {
        DownloadResource resource = downloadResourceService.getResourceByTemplate(CONSENT_TEMPLATE);
        return ResponseEntity.ok()
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFileName())
                   .body(resource.getResource());
    }
}
