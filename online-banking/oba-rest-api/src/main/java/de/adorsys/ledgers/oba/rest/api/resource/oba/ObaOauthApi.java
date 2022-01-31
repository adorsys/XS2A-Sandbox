/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

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
