package de.adorsys.psd2.sandbox.admin.rest.api.resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(tags = "Admin Email verification management")
public interface AdminEmailVerificationRestApi {
    String BASE_PATH = "/admin/sca";

    @PostMapping
    @ApiOperation(value = "Send email for verification", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Void> sendEmailVerification(@RequestParam("email") String email);

    @GetMapping("/email")
    @ApiOperation(value = "Confirm email")
    ResponseEntity<Void> confirmVerificationToken(@RequestParam("verificationToken") String token);
}
