package de.adorsys.ledgers.oba.rest.api.resource.oba;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@Api(value = ObaAuthorizationApi.BASE_PATH, tags = "Online Banking Authorization", description = "Provides access to online banking")
public interface ObaAuthorizationApi {
    String BASE_PATH = "/api/v1/login";

    /**
     * @param login users login
     * @param pin   users pin
     */
    @PostMapping
    @ApiOperation(value = "Perform Online Banking Login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);
}
