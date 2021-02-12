package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends AbstractAuthFilter {
    private final AuthRequestInterceptor authInterceptor;
    private final KeycloakTokenService tokenService;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        String bearerToken = resolveBearerToken(request);
        authInterceptor.setAccessToken(null);
        if (StringUtils.isBlank(bearerToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                authInterceptor.setAccessToken(bearerToken);

                BearerTokenTO validateResponse = tokenService.validate(bearerToken);

                BearerTokenTO token = Optional.ofNullable(validateResponse)
                                          .orElseThrow(() -> ObaException.builder()
                                                                 .obaErrorCode(ObaErrorCode.ACCESS_FORBIDDEN)
                                                                 .devMessage("Couldn't get bearer token").build());

                fillSecurityContext(token);
            } catch (FeignException | ObaException e) {
                handleAuthenticationFailure(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(obtainFromHeader(request, HttpHeaders.AUTHORIZATION))
                   .filter(StringUtils::isNotBlank)
                   .filter(t -> StringUtils.startsWithIgnoreCase(t, SecurityConstant.BEARER_TOKEN_PREFIX))
                   .map(t -> StringUtils.substringAfter(t, SecurityConstant.BEARER_TOKEN_PREFIX))
                   .orElse(null);
    }
}
