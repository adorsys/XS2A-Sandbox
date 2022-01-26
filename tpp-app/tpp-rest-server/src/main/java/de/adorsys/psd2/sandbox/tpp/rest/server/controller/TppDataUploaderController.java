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
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.DownloadResourceService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.RestExecutionService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TestsDataGenerationService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.Currency;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TppDataUploaderRestApi.BASE_PATH)
public class TppDataUploaderController implements TppDataUploaderRestApi {
    private static final String FILE_NAME = "transactions_template.csv";

    private final RestExecutionService restExecutionService;
    private final ParseService parseService;
    private final TestsDataGenerationService generationService;
    private final IbanGenerationService ibanGenerationService;
    private final TransactionService transactionService;
    private final DownloadResourceService downloadResourceService;

    @Override
    public ResponseEntity<String> uploadData(MultipartFile file) {
        log.info("Update file received");
        DataPayload parsed = parseService.getDataFromFile(file, new TypeReference<DataPayload>() {
        }).orElseThrow(() -> new TppException("Could not parse data", 400));

        restExecutionService.updateLedgers(parsed);
        return ResponseEntity.ok("Data successfully updated");
    }

    @Override
    public ResponseEntity<Resource> generateData(boolean generatePayments, String currency) {
        log.info("Request to create test data received");

        byte[] bytes = generationService.generate(generatePayments, Currency.getInstance(currency));

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));
        HttpHeaders headers = getExportFileHttpHeaders();
        return ResponseEntity.ok()
                   .headers(headers)
                   .contentLength(bytes.length)
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .body(resource);
    }

    @Override
    public ResponseEntity<String> generateIban(String tppId) {
        return ResponseEntity.ok(ibanGenerationService.generateNextIban(tppId));
    }

    @Override
    public ResponseEntity<Map<String, String>> uploadTransactions(MultipartFile file) {
        log.info("uploading transactions");
        Map<String, String> response = transactionService.uploadUserTransaction(file);
        log.info("upload response contains {} errors", response.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Resource> downloadTransactionTemplate() {
        return ResponseEntity.ok()
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + FILE_NAME)
                   .body(downloadResourceService.getResourceByTemplate(FILE_NAME));
    }

    private HttpHeaders getExportFileHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Content-Disposition", "attachment; filename=nisp_data.yml");
        return headers;
    }
}
