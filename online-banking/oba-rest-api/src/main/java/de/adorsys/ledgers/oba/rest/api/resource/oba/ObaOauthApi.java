package de.adorsys.ledgers.oba.rest.api.resource.oba;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = ObaOauthApi.BASE_PATH, tags = "Online Banking Oauth Authorization")
public interface ObaOauthApi {
    String BASE_PATH = "/oauth";

    @PostMapping("/authorise")
    @ApiOperation(value = "Oauth authorise")
    void oauthCode(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin, @RequestParam("redirect_uri") String redirectUri);

    @PostMapping("/token")
    @ApiOperation(value = "Oauth token")
    void oauthToken(@RequestHeader(value = "code") String code);
}
