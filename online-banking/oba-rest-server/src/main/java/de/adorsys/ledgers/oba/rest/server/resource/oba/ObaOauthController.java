/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthCodeResponseTO;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaOauthApi;
import de.adorsys.ledgers.oba.rest.server.config.security.OauthServerLinkResolver;
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
        return oauthRestClient.oauthToken(code);
    }

    @Override
    public OauthServerInfoTO oauthServerInfo(String redirectId, String paymentId, String consentId, String cancellationId) {
        return new OauthServerLinkResolver(oauthRestClient.oauthServerInfo().getBody(), paymentId, consentId, cancellationId, redirectId, obaBeBaseUri, obaFeBaseUri).resolve();
    }
}
