package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.AUTHORIZATION_HEADER;
import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.BEARER_TOKEN_PREFIX;

public class TokenAuthenticationFilter extends AbstractAuthFilter {
    private final UserMgmtRestClient ledgersUserMgmt;
    private final AuthRequestInterceptor authInterceptor;

    public TokenAuthenticationFilter(UserMgmtRestClient ledgersUserMgmt, AuthRequestInterceptor authInterceptor) {
        this.ledgersUserMgmt = ledgersUserMgmt;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String bearerToken = resolveBearerToken(request);

        if (StringUtils.isBlank(bearerToken)) {
            chain.doFilter(request, response);
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
        chain.doFilter(request, response);
    }

    private String resolveBearerToken(HttpServletRequest request) {
        return Optional.ofNullable(obtainFromHeader(request, AUTHORIZATION_HEADER))
                   .filter(StringUtils::isNotBlank)
                   .filter(t -> StringUtils.startsWithIgnoreCase(t, BEARER_TOKEN_PREFIX))
                   .map(t -> StringUtils.substringAfter(t, BEARER_TOKEN_PREFIX))
                   .orElse(null);
    }
}
