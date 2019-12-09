package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Api(tags = "TPP data uploader")
public interface TppDataUploaderRestApi {
    String BASE_PATH = TppRestApi.BASE_PATH + "/data";

    @ApiOperation(value = "Upload YAML file with basic test data", authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/upload")
    ResponseEntity<String> uploadData(@RequestBody MultipartFile file);

    @ApiOperation(value = "Generate test data and upload it to Ledgers", authorizations = @Authorization(value = "apiKey"))
    @GetMapping(value = "/generate")
    ResponseEntity<Resource> generateData(@RequestParam boolean generatePayments);

    @ApiOperation(value = "Generate random IBAN", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/generate/iban")
    ResponseEntity<String> generateIban();

    @ApiOperation(value = "Upload CSV file with transactions list", authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/upload/transactions")
    ResponseEntity<Map<String, String>> uploadTransactions(@RequestBody MultipartFile file);

    @GetMapping("/example")
    @ApiOperation(value = "Download transaction template", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Resource> downloadTransactionTemplate();
}
