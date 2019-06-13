package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Api(tags = "TPP registration")
public interface TppRestApi {
    String BASE_PATH = "/tpp";

    @ApiOperation(value = "Login for TPP", authorizations = @Authorization(value = "apiKey"))
    @PostMapping(value = "/login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @ApiOperation(value = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody TppInfo tppInfo);
}
