package de.adorsys.psd2.sandbox.admin.rest.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "Admin data API")
public interface AdminDataRestApi {
    String BASE_PATH = AdminBaseRestApi.BASE_PATH + "/data";

    @ApiOperation(value = "Generate random IBAN", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/generate/iban")
    ResponseEntity<String> generateIban(@RequestParam("tppId") String tppId);
}
