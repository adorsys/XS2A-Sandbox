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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService {

    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;
    private final KeycloakTokenService tokenService;
    @Override
    public UserAuthentication getAuthentication(String accessToken) {
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        BearerTokenTO bearerToken = tokenService.validate(accessToken);

        if (bearerToken == null) {
            debug();
            return null;
        }
        return new UserAuthentication(bearerToken);
    }

    private void debug() {
        if (log.isDebugEnabled()) {
            log.debug("Token is not valid.");
        }
    }

    @Override
    public GlobalScaResponseTO login(String login, String pin, String authorizationId) {
        if (StringUtils.isBlank(authorizationId) || StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            throw ObaException.builder()
                      .devMessage("Authorization Id is missing!")
                      .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                      .build();
        }
        GlobalScaResponseTO response = login(login, pin);
        response.setAuthorisationId(authorizationId);
        return response;
    }

    @Override
    public GlobalScaResponseTO login(String login, String pin) {
        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            throw ObaException.builder()
                      .devMessage("Authorization Id is missing!")
                      .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                      .build();
        }
        try {
            BearerTokenTO token = tokenService.login(login, pin);
            return getScaResponseTO(token.getAccess_token());
        } catch (Exception e) {
            throw throwException(e);
        }
    }


    private GlobalScaResponseTO getScaResponseTO(String tokenString) {
        BearerTokenTO token = tokenService.validate(tokenString);
        authInterceptor.setAccessToken(token.getAccess_token());
        UserTO user = ledgersUserMgmt.getUser().getBody();
        GlobalScaResponseTO response = new GlobalScaResponseTO();
        response.setBearerToken(token);
        response.setScaMethods(requireNonNull(user).getScaUserData());
        response.setScaStatus(ScaStatusTO.PSUIDENTIFIED);
        authInterceptor.setAccessToken(null);
        return response;
    }

    private ObaException throwException(Exception e) {
        log.error(e.getMessage());
        authInterceptor.setAccessToken(null);
        return ObaException.builder()
                   .devMessage(e.getMessage())
                   .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                   .build();
    }
}
