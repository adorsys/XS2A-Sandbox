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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.util.DateUtils;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.sandbox.auth.ErrorResponse;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static de.adorsys.psd2.sandbox.auth.SecurityConstant.*;

@SuppressWarnings("PMD.TooManyStaticImports")
@Slf4j
abstract class AbstractAuthFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public static final String INVALID_REFRESH_TOKEN = "invalid refresh token";

    protected void handleAuthenticationFailure(HttpServletResponse response, Exception e, HttpStatus status) throws IOException {
        log.error(e.getMessage());

        Map<String, String> data = new ErrorResponse()
            .buildContent(status.value(), status.getReasonPhrase());
        response.setStatus(status.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }

    protected String obtainFromHeader(HttpServletRequest request, String headerKey) {
        return request.getHeader(headerKey);
    }

    protected boolean authenticationIsRequired() {
        Authentication existingAuth = SecurityContextHolder.getContext().getAuthentication();
        return isNotAuthenticated(existingAuth) || isNotMiddlewareAuthentication(existingAuth);
    }

    protected void fillSecurityContext(BearerTokenTO token) {
        SecurityContextHolder.getContext()
            .setAuthentication(new MiddlewareAuthentication(token.getAccessTokenObject(), token, buildGrantedAuthorities(token.getAccessTokenObject())));
    }

    protected String resolveBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(obtainFromHeader(request, AUTHORIZATION_HEADER))
            .filter(StringUtils::isNotBlank)
            .filter(t -> StringUtils.startsWithIgnoreCase(t, BEARER_TOKEN_PREFIX))
            .map(t -> StringUtils.substringAfter(t, BEARER_TOKEN_PREFIX))
            .orElse(null);
    }


    private boolean isNotAuthenticated(Authentication existingAuth) {
        return existingAuth == null || !existingAuth.isAuthenticated();
    }

    private boolean isNotMiddlewareAuthentication(Authentication existingAuth) {
        return !(existingAuth instanceof MiddlewareAuthentication);
    }

    private List<GrantedAuthority> buildGrantedAuthorities(AccessTokenTO accessTokenTO) {
        return accessTokenTO.getRole() != null
            ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + accessTokenTO.getRole().name()))
            : Collections.emptyList();
    }


    protected void removeCookie(HttpServletResponse response, String cookieName, boolean isSecure) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(isSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public void addRefreshTokenCookie(HttpServletResponse response, String jwtId, String refreshToken, boolean isSecure) {
        String cookieName = REFRESH_TOKEN_COOKIE_PREFIX + jwtId;
        Cookie cookie = new Cookie(cookieName, refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(isSecure);
        cookie.setMaxAge(expiredTimeInSec(refreshToken).intValue());
        cookie.setPath("/");
        response.addCookie(cookie);
    }


    protected void refreshUserSession(BearerTokenTO bearerTokenTO, HttpServletResponse response, boolean isSecure) {
        String access_token = bearerTokenTO.getAccess_token();
        addRefreshTokenCookie(response, jwtId(access_token), bearerTokenTO.getRefresh_token(), isSecure);
        addBearerTokenHeader(access_token, response);
    }

    protected void addBearerTokenHeader(String token, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, token);
    }

    public String getCookieValue(HttpServletRequest request, String name) {
        return Optional.ofNullable(WebUtils.getCookie(request, name))
            .map(Cookie::getValue)
            .orElseThrow(() -> new AccessDeniedException(INVALID_REFRESH_TOKEN));
    }

    @SneakyThrows
    public String jwtId(String jwtToken) {
        return JWTParser.parse(jwtToken).getJWTClaimsSet().getJWTID();
    }

    @SneakyThrows
    public boolean isExpiredToken(String jwtToken) {
        Date expirationTime = JWTParser.parse(jwtToken).getJWTClaimsSet().getExpirationTime();
        return Optional.ofNullable(expirationTime)
            .map(d -> d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
            .map(d -> d.isBefore(LocalDateTime.now()))
            .orElse(true);
    }


    @SneakyThrows
    public Long expiredTimeInSec(String jwtToken) {
        Date issueTime = JWTParser.parse(jwtToken).getJWTClaimsSet().getIssueTime();
        Date expirationTime = JWTParser.parse(jwtToken).getJWTClaimsSet().getExpirationTime();
        return DateUtils.toSecondsSinceEpoch(expirationTime) - DateUtils.toSecondsSinceEpoch(issueTime);
    }

}
