package de.adorsys.ledgers.oba.rest.server.auth.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.*;

@RequiredArgsConstructor
public class LoginAuthenticationFilter extends AbstractAuthFilter {
    private final UserMgmtRestClient userMgmtRestClient;

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
                ResponseEntity<SCALoginResponseTO> loginResponse = userMgmtRestClient.authorise(login, pin, UserRoleTO.CUSTOMER);

                BearerTokenTO bearerTokenTO = Optional.ofNullable(loginResponse.getBody())
                                                  .map(SCAResponseTO::getBearerToken)
                                                  .orElseThrow(() -> new RestException("Couldn't get bearer token"));

                fillSecurityContext(bearerTokenTO);
                addBearerTokenHeader(bearerTokenTO.getAccess_token(), response);
            } catch (FeignException | RestException e) {
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

