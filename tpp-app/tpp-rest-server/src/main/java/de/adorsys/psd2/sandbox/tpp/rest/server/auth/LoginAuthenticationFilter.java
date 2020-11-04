package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;

import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.*;

@RequiredArgsConstructor
public class LoginAuthenticationFilter extends AbstractAuthFilter {
    private final KeycloakTokenService tokenService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String login = obtainFromHeader(request, USER_LOGIN);
        String pin = obtainFromHeader(request, USER_PIN);

        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            chain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                BearerTokenTO bearerTokenTO = tokenService.login(login, pin);
                bearerTokenTO = tokenService.validate(bearerTokenTO.getAccess_token());
                if (!EnumSet.of(UserRoleTO.SYSTEM, UserRoleTO.STAFF).contains(bearerTokenTO.getAccessTokenObject().getRole())) {
                    handleAuthenticationFailure(response, new IllegalAccessException(String.format("User %s is missing required Role to login", login)));
                    return;
                }
                fillSecurityContext(bearerTokenTO);
                addBearerTokenHeader(bearerTokenTO.getAccess_token(), response);
            } catch (FeignException | RestException e) {
                handleAuthenticationFailure(response, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private void addBearerTokenHeader(String token, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, token);
    }
}

