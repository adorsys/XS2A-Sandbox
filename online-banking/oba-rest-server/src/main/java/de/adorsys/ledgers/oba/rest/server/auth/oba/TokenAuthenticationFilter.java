package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.BEARER_TOKEN_PREFIX;

@RequiredArgsConstructor
public class TokenAuthenticationFilter extends AbstractAuthFilter {
    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = resolveBearerToken(request);

        if (StringUtils.isBlank(bearerToken)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                authInterceptor.setAccessToken(bearerToken);

                ResponseEntity<BearerTokenTO> validateResponse = ledgersUserMgmt.validate(bearerToken);

                BearerTokenTO token = Optional.ofNullable(validateResponse.getBody())
                                          .orElseThrow(() -> new RestException("Couldn't get bearer token"));

                fillSecurityContext(token);
            } catch (FeignException | RestException e) {
                handleAuthenticationFailure(response, e);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(obtainFromHeader(request, HttpHeaders.AUTHORIZATION))
                   .filter(StringUtils::isNotBlank)
                   .filter(t -> StringUtils.startsWithIgnoreCase(t, BEARER_TOKEN_PREFIX))
                   .map(t -> StringUtils.substringAfter(t, BEARER_TOKEN_PREFIX))
                   .orElse(null);
    }
}
