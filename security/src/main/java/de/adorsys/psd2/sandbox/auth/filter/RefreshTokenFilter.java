/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.auth.filter;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.sandbox.auth.SecurityConstant;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RefreshTokenFilter extends AbstractAuthFilter {

    private final KeycloakTokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String bearerToken = resolveBearerToken(request);
        try {
            if (StringUtils.isNotBlank(bearerToken) && isExpiredToken(bearerToken)) {
                BearerTokenTO bearerTokenTO = refreshAccessToken(request, response);
                refreshUserSession(bearerTokenTO, response, request.isSecure());
                HttpServletRequest wrappedRequest = new RefreshTokenRequestWrapper(request, bearerTokenTO.getAccess_token());
                chain.doFilter(wrappedRequest, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (FeignException | AccessDeniedException e) {
            handleAuthenticationFailure(response, e, HttpStatus.UNAUTHORIZED);
        }
    }


    private BearerTokenTO refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String bearerToken = resolveBearerToken(request);
        String jwtid = jwtId(bearerToken);
        String oldRefreshTokenCookieName = SecurityConstant.REFRESH_TOKEN_COOKIE_PREFIX + jwtid;
        String refreshToken = getCookieValue(request, oldRefreshTokenCookieName);
        if (isExpiredToken(refreshToken)) {
            throw new AccessDeniedException("Refresh token is expired !");
        }

        BearerTokenTO bearerTokenTO = tokenService.refreshToken(refreshToken);
        removeCookie(response, oldRefreshTokenCookieName, request.isSecure());
        return bearerTokenTO;
    }


    private static class RefreshTokenRequestWrapper extends HttpServletRequestWrapper {
        private final String accessToken;

        RefreshTokenRequestWrapper(HttpServletRequest request, String accessToken) {
            super(request);
            this.accessToken = accessToken;
        }

        @Override
        public String getHeader(String name) {
            if (SecurityConstant.AUTHORIZATION_HEADER.equals(name)) {
                return SecurityConstant.BEARER_TOKEN_PREFIX + accessToken;
            } else {
                return super.getHeader(name);
            }
        }
    }
}
