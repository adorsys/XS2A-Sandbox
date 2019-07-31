package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(tags = "TPP reset password")
public interface TppResetPasswordRestApi {
    String BASE_PATH = "/tpp/password";

    @PostMapping
    @ApiOperation(value = "Send code for user password reset")
    ResponseEntity<SendCode> sendCode(@RequestBody ResetPassword resetPassword);

    @PutMapping
    @ApiOperation(value = "Update user password")
    ResponseEntity<UpdatePassword> updatePassword(@RequestBody ResetPassword resetPassword);
}
