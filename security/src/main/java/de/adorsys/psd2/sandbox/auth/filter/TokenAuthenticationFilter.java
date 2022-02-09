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

package de.adorsys.psd2.sandbox.auth.filter;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends AbstractAuthFilter {
    private final AuthRequestInterceptor authInterceptor;
    private final KeycloakTokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        authInterceptor.setAccessToken(null);
        String bearerToken = resolveBearerToken(request);

        if (StringUtils.isBlank(bearerToken)) {
            chain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                authInterceptor.setAccessToken(bearerToken);

                BearerTokenTO validateResponse = tokenService.validate(bearerToken);

                BearerTokenTO token = Optional.ofNullable(validateResponse)
                    .orElseThrow(() -> new AccessDeniedException("Invalid token !"));

                fillSecurityContext(token);
            } catch (FeignException | AccessDeniedException e) {
                handleAuthenticationFailure(response, e, HttpStatus.UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
