package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.oauth.GrantTypeTO;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthCodeResponseTO;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaOauthApi;
import de.adorsys.ledgers.oba.service.impl.service.OauthServerLinkResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ObaOauthApi.BASE_PATH)
public class ObaOauthController implements ObaOauthApi {
    @Value("${oba.url:http://localhost:4400}")
    private String obaFeBaseUri;
    @Value("${self.url:http://localhost:8090}")
    private String obaBeBaseUri;

    private final OauthRestClient oauthRestClient;

    @Override
    public ResponseEntity<OauthCodeResponseTO> oauthCode(String login, String pin, String redirectUri) {
        return oauthRestClient.oauthCode(login, pin, redirectUri);
    }

    @Override
    public ResponseEntity<BearerTokenTO> oauthToken(String code) {
        return oauthRestClient.oauthToken(GrantTypeTO.AUTHORISATION_CODE, code);
    }

    @Override
    public OauthServerInfoTO oauthServerInfo(String redirectId, String paymentId, String consentId, String cancellationId) {
        return new OauthServerLinkResolver(oauthRestClient.oauthServerInfo().getBody(), paymentId, consentId, cancellationId, redirectId, obaBeBaseUri, obaFeBaseUri).resolve();
    }
}
