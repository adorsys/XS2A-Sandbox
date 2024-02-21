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

package de.adorsys.ledgers.oba.rest.server.config.security;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Setter
public class OauthServerLinkResolver {
    private OauthServerInfoTO info;
    private Map<String, String> objectIds = new HashMap<>();
    private Map<String, String> pathPart = new HashMap<>();
    private String redirectId;
    private String requestParameter;
    private String obaFeBaseUri;
    private String obaBeBaseUri;

    public OauthServerLinkResolver(OauthServerInfoTO info, String paymentId, String consentId, String cancellationId, String redirectId, String obaBeBaseUri, String obaFeBaseUri) {
        this.info = info;
        objectIds.put("paymentId", paymentId);
        objectIds.put("encryptedConsentId", consentId);
        objectIds.put("cancellationId", cancellationId);

        this.requestParameter = resolvePresentParameter();

        pathPart.put("paymentId", "payment-initiation");
        pathPart.put("encryptedConsentId", "account-information");
        pathPart.put("cancellationId", "payment-cancellation");
        this.redirectId = redirectId;
        this.obaBeBaseUri = obaBeBaseUri;
        this.obaFeBaseUri = obaFeBaseUri;
    }

    public OauthServerInfoTO resolve() {
        String authUri = StringUtils.isNotBlank(redirectId) && requestParameter != null
                             ? resolveParametrizedAuthUri()
                             : resolveNonParametrizedAuthUri();
        info.setAuthorizationEndpoint(authUri);
        info.setTokenEndpoint(resolveTokenUri());
        return info;
    }

    private String resolveParametrizedAuthUri() {
        return UriComponentsBuilder
                   .fromUriString(obaFeBaseUri)
                   .pathSegment(pathPart.get(requestParameter))
                   .pathSegment("login")
                   .queryParam("redirectId", redirectId)
                   .queryParam(requestParameter, objectIds.get(requestParameter))
                   .queryParam("oauth2", true)
                   .build()
                   .toUriString();
    }

    private String resolveNonParametrizedAuthUri() {
        return UriComponentsBuilder
                   .fromUriString(obaFeBaseUri)
                   .pathSegment("auth/authorize")
                   .queryParam("redirect_uri=")
                   .build()
                   .toUriString();
    }

    private String resolveTokenUri() {
        return UriComponentsBuilder
                   .fromUriString(obaBeBaseUri)
                   .pathSegment("oauth/token")
                   .build()
                   .toUriString();
    }

    private String resolvePresentParameter() {
        return objectIds.entrySet().stream()
                   .filter(e -> StringUtils.isNotBlank(e.getValue()))
                   .map(Map.Entry::getKey)
                   .findFirst()
                   .orElse(null);
    }
}
