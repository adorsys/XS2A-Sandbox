package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.*;
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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TppDataUploaderRestApi.BASE_PATH)
public class TppDataUploaderController implements TppDataUploaderRestApi {
    private final RestExecutionService restExecutionService;
    private final ParseService parseService;
    private final TestsDataGenerationService generationService;
    private final IbanGenerationService ibanGenerationService;
    private final TransactionService transactionService;

    @Override
    public ResponseEntity<String> uploadData(MultipartFile file) {
        log.info("Update file received");
        DataPayload parsed = parseService.getDataFromFile(file, new TypeReference<DataPayload>() {
        }).orElseThrow(() -> new TppException("Could not parse data", 400));

        restExecutionService.updateLedgers(parsed);
        return ResponseEntity.ok("Data successfully updated");
    }

    @Override
    public ResponseEntity<Resource> generateData(boolean generatePayments) {
        log.info("Request to create test data received");

        byte[] bytes = generationService.generate(generatePayments);

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(bytes));
        HttpHeaders headers = getExportFileHttpHeaders();
        return ResponseEntity.ok()
                   .headers(headers)
                   .contentLength(bytes.length)
                   .contentType(MediaType.APPLICATION_OCTET_STREAM)
                   .body(resource);
    }

    @Override
    public ResponseEntity<String> generateIban() {
        return ResponseEntity.ok(ibanGenerationService.generateRandomIban());
    }

    @Override
    public ResponseEntity<Map<String, String>> uploadTransactions(MultipartFile file) {
        log.info("uploading transactions");
        Map<String, String> response = transactionService.uploadUserTransaction(file);
        log.info("upload response contains {} errors", response.size());
        return ResponseEntity.ok(response);
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
