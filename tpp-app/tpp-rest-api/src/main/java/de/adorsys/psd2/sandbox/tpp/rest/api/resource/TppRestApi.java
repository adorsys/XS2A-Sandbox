package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "TPP main API")
public interface TppRestApi {
    String BASE_PATH = "/tpp";

    @ApiOperation(value = "Login for TPP")
    @PostMapping(value = "/login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @ApiOperation(value = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @ApiOperation(value = "Remove Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping
    ResponseEntity<Void> remove();

    @ApiOperation(value = "Remove transactions for account in Tpp", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/account/{iban}")
    ResponseEntity<Void> transactions(@PathVariable String iban);
}
