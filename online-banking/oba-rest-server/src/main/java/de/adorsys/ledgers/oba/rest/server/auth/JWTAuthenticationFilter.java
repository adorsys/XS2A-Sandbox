/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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

package de.adorsys.ledgers.oba.rest.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.service.api.domain.UserAuthentication;
import de.adorsys.ledgers.oba.service.api.service.TokenAuthenticationService;
import de.adorsys.psd2.sandbox.auth.ErrorResponse;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private static final List<String> EXCLUDED_URLS = Arrays.asList("/**/auth", "/**/login");
    private static final AntPathMatcher matcher = new AntPathMatcher();

    private final TokenAuthenticationService tokenAuthenticationService;
    private final AuthRequestInterceptor authInterceptor;
    private final ObjectMapper objectMapper;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        authInterceptor.setAccessToken(null);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            UserAuthentication userAuthentication;
            try {
                userAuthentication = tokenAuthenticationService.getAuthentication(readAccessTokenHeader(request));
            } catch (AccessDeniedException e) {
                Map<String, String> errorMap = new ErrorResponse()
                                                   .buildContent(401, e.getMessage());
                response.setStatus(401);
                response.getWriter().write(objectMapper.writeValueAsString(errorMap));
                return;
            }
            if (userAuthentication != null) {
                BearerTokenTO bearerToken = userAuthentication.getBearerToken();
                AccessTokenTO token = bearerToken.getAccessTokenObject();
                SecurityContextHolder.getContext().setAuthentication(new MiddlewareAuthentication(token, bearerToken, buildAuthorities(token)));
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_URLS.stream()
                   .anyMatch(p -> matcher.match(p, request.getServletPath()));
    }


    private String readAccessTokenHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("authorization"))
                   .map(s -> s.replace("Bearer ", ""))
                   .orElse(null);
    }

    private List<GrantedAuthority> buildAuthorities(AccessTokenTO token) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (token.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + token.getRole().name()));
        }
        return authorities;
    }
}
