package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import com.google.common.base.Strings;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.ACCESS_TOKEN;

public class TokenAuthenticationFilter extends AbstractAuthFilter {
    private final UserMgmtRestClient ledgersUserMgmt;

    public TokenAuthenticationFilter(UserMgmtRestClient ledgersUserMgmt) {
        this.ledgersUserMgmt = ledgersUserMgmt;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String accessToken = obtainFromHeader(request, ACCESS_TOKEN);

        if (Strings.isNullOrEmpty(accessToken)) {
            chain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                ResponseEntity<BearerTokenTO> validateResponse = ledgersUserMgmt.validate(accessToken);

                BearerTokenTO token = Optional.ofNullable(validateResponse.getBody())
                                          .orElseThrow(() -> new RestException("Couldn't get bearer token"));

                fillSecurityContext(token);

            } catch (FeignException | RestException e) {
                handleAuthenticationFailure(response, e);
            }
        }
        chain.doFilter(request, response);
    }
}
