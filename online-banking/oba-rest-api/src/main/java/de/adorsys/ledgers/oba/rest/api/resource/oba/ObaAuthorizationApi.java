package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.security.ResetPassword;
import de.adorsys.ledgers.security.SendCode;
import de.adorsys.ledgers.security.UpdatePassword;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(value = ObaAuthorizationApi.BASE_PATH, tags = "Online Banking Authorization. Provides access to online banking")
public interface ObaAuthorizationApi {
    String BASE_PATH = "/api/v1";

    /**
     * @param login users login
     * @param pin   users pin
     */
    @PostMapping("/login")
    @ApiOperation(value = "Perform Online Banking Login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @ApiOperation(value = "Get current user", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/me")
    ResponseEntity<UserTO> getSelf();

    @ApiOperation(value = "Edit current user", authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/me")
    ResponseEntity<Void> editSelf(@RequestBody UserTO user);

    @PostMapping("/password")
    @ApiOperation(value = "Send code for user password reset")
    ResponseEntity<SendCode> sendCode(@RequestBody ResetPassword resetPassword);

    @PutMapping("/password")
    @ApiOperation(value = "Update user password")
    ResponseEntity<UpdatePassword> updatePassword(@RequestBody ResetPassword resetPassword);
}
