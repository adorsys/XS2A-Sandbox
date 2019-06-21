package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.RestExecutionService;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TestsDataGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(TppDataUploaderRestApi.BASE_PATH)
public class TppDataUploaderController implements TppDataUploaderRestApi {
    private final RestExecutionService restExecutionService;
    private final ParseService parseService;
    private final TestsDataGenerationService generationService;
    private final IbanGenerationService ibanGenerationService;

    @Override
    public ResponseEntity<String> uploadData(MultipartFile file) {
        log.info("Update file received");
        DataPayload parsed = parseService.getDataFromFile(file);
        if (parsed == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not parse data");
        }
        log.info("Read data is successful");
        return restExecutionService.updateLedgers(parsed)
                   ? ResponseEntity.ok("Data successfully updated")
                   : new ResponseEntity<>("Could not update data.", HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    public ResponseEntity<Resource> generateData() {
        log.info("Request to create test data received");

        byte[] bytes = generationService.generate();

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


    private HttpHeaders getExportFileHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return headers;
    }
}
