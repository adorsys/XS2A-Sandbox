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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.psd2.sandbox.cms.connector.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.cms.connector.api.service.ConsentService;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppConsentRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
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
    private static final String FILE_NAME = "consents_template.yml";

    private final ConsentService tppConsentService;
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
        return ResponseEntity.ok()
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME)
                   .body(downloadResourceService.getResourceByTemplate(FILE_NAME));
    }
}
