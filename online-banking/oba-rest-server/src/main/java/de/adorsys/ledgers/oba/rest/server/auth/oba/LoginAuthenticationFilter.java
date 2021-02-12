package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.*;

@RequiredArgsConstructor
public class LoginAuthenticationFilter extends AbstractAuthFilter {
    private final KeycloakTokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String login = obtainFromHeader(request, USER_LOGIN);
        String pin = obtainFromHeader(request, USER_PIN);

        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                BearerTokenTO bearerTokenTO = tokenService.login(login, pin);
                bearerTokenTO = tokenService.validate(bearerTokenTO.getAccess_token());

                fillSecurityContext(bearerTokenTO);
                addBearerTokenHeader(bearerTokenTO.getAccess_token(), response);
            } catch (FeignException e) {
                handleAuthenticationFailure(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void addBearerTokenHeader(String token, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, token);
    }
}

