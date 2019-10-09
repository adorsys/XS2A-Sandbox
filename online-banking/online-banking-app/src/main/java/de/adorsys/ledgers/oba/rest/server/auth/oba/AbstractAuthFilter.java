package de.adorsys.ledgers.oba.rest.server.auth.oba;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
public abstract class AbstractAuthFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    protected void handleAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        doAuthenticationFailure(response, message);
    }

    protected void handleAuthenticationFailure(HttpServletResponse response, Exception e) throws IOException {
        doAuthenticationFailure(response, e.getMessage());
    }

    private void doAuthenticationFailure(HttpServletResponse response, String message) throws IOException {
        log.error(message);

        Map<String, String> data = new ErrorResponse()
                                       .buildContent(UNAUTHORIZED.value(), message);

        response.setStatus(UNAUTHORIZED.value());
        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().println(objectMapper.writeValueAsString(data));
    }

    protected String obtainFromHeader(HttpServletRequest request, String headerKey) {
        return request.getHeader(headerKey);
    }

    protected String obtainFromRequest(HttpServletRequest request, String param) {
        return request.getParameter(param);
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
