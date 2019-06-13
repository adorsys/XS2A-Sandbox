package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
abstract class AbstractAuthFilter extends GenericFilterBean {
    private ObjectMapper objectMapper = new ObjectMapper();

    protected void handleAuthenticationFailure(HttpServletResponse response, Exception e) throws IOException {
        log.error(e.getMessage());

        Map<String, Object> data = new HashMap<>();
        data.put("message", e.getMessage());
        data.put("code", UNAUTHORIZED.value());

        response.setStatus(UNAUTHORIZED.value());
        response.getOutputStream()
            .println(objectMapper.writeValueAsString(data));
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
}
