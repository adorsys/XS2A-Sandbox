package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthCodeResponseTO;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = ObaOauthApi.BASE_PATH, tags = "Online Banking Oauth Authorization")
public interface ObaOauthApi {
    String BASE_PATH = "/oauth";

    @PostMapping("/authorise")
    @ApiOperation(value = "Oauth authorise")
    ResponseEntity<OauthCodeResponseTO> oauthCode(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin, @RequestParam("redirect_uri") String redirectUri);

    @PostMapping("/token")
    @ApiOperation(value = "Oauth token")
    ResponseEntity<BearerTokenTO> oauthToken(@RequestParam(value = "code") String code);

    @GetMapping("/authorization-server")
    @ApiOperation(value = "Server info")
    OauthServerInfoTO oauthServerInfo(
        @RequestParam(required = false) String redirectId,
        @RequestParam(required = false) String paymentId,
        @RequestParam(required = false) String consentId,
        @RequestParam(required = false) String cancellationId
    );
}
