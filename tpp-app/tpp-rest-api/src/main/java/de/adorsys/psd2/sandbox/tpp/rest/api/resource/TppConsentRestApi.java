package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = "TPP Consent management")
public interface TppConsentRestApi {
    String BASE_PATH = "/tpp/consent";

    @PutMapping
    @ApiOperation(value = "Generate list of consents")
    ResponseEntity<List<String>> generateConsents(@RequestBody MultipartFile file);

    @GetMapping("/example")
    @ApiOperation(value = "Download consent template", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Resource> downloadConsentTemplate();
}
