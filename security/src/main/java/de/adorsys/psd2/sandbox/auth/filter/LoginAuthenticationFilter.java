package de.adorsys.psd2.sandbox.auth.filter;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.sandbox.auth.LoginAuthorization;
import de.adorsys.psd2.sandbox.auth.SecurityConstant;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class LoginAuthenticationFilter extends AbstractAuthFilter {
    private final KeycloakTokenService tokenService;
    private final LoginAuthorization loginAuthorization;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        String login = obtainFromHeader(request, SecurityConstant.USER_LOGIN);
        String pin = obtainFromHeader(request, SecurityConstant.USER_PIN);

        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin)) {
            chain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                BearerTokenTO bearerTokenTO = tokenService.login(login, pin);
                addRefreshTokenCookie(response, jwtId(bearerTokenTO.getAccess_token()), bearerTokenTO.getRefresh_token(), request.isSecure());
                bearerTokenTO = tokenService.validate(bearerTokenTO.getAccess_token());
                if (!loginAuthorization.canLogin(bearerTokenTO)) {
                    handleAuthenticationFailure(response, new IllegalAccessException(String.format("User %s is missing required Role to login", login)), HttpStatus.FORBIDDEN);
                    return;
                }
                fillSecurityContext(bearerTokenTO);
                addBearerTokenHeader(bearerTokenTO.getAccess_token(), response);
            } catch (FeignException e) {
                handleAuthenticationFailure(response, e, HttpStatus.UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }


}

