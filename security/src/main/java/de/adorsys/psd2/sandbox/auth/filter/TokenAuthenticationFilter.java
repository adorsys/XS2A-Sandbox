package de.adorsys.psd2.sandbox.auth.filter;

import de.adorsys.ledgers.keycloak.client.api.KeycloakTokenService;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
                handleAuthenticationFailure(response, e);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
