package de.adorsys.ledgers.oba.rest.server.auth;


import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenAuthenticationService {

    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;

    public Authentication getAuthentication(HttpServletRequest request) {
        String accessToken = readAccessTokenCookie(request);
        if (StringUtils.isBlank(accessToken)) {
            return null;
        }
        BearerTokenTO bearerToken = null;
        try {
            authInterceptor.setAccessToken(accessToken);
            ResponseEntity<BearerTokenTO> responseEntity = ledgersUserMgmt.validate(accessToken);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                bearerToken = responseEntity.getBody();
            }
        } finally {
            authInterceptor.setAccessToken(null);
        }

        if (bearerToken == null) {
            debug("Token is not valid.");
            return null;
        }

        // process roles
        AccessTokenTO token = bearerToken.getAccessTokenObject();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (token.getRole() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + token.getRole().name()));
        }

        return new MiddlewareAuthentication(token.getSub(), bearerToken, authorities);
    }

    public String readAccessTokenCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("ACCESS_TOKEN".equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void debug(String s) {
        if (log.isDebugEnabled()) {
            log.debug(s);
        }
    }
}
