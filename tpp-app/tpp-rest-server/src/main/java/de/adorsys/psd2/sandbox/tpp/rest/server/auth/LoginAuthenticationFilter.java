package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import com.google.common.base.Strings;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserCredentialsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
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

import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.*;

public class LoginAuthenticationFilter extends AbstractAuthFilter {
    private UserMgmtStaffRestClient userMgmtStaffRestClient;

    public LoginAuthenticationFilter(UserMgmtStaffRestClient userMgmtStaffRestClient) {
        this.userMgmtStaffRestClient = userMgmtStaffRestClient;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String login = obtainFromHeader(request, USER_LOGIN);
        String pin = obtainFromHeader(request, USER_PIN);

        if (Strings.isNullOrEmpty(login) || Strings.isNullOrEmpty(pin)) {
            chain.doFilter(request, response);
            return;
        }

        if (authenticationIsRequired()) {
            try {
                ResponseEntity<SCALoginResponseTO> loginResponse = userMgmtStaffRestClient.login(new UserCredentialsTO(login, pin, UserRoleTO.STAFF));

                BearerTokenTO token = Optional.ofNullable(loginResponse.getBody())
                                          .map(SCAResponseTO::getBearerToken)
                                          .orElseThrow(() -> new RestException("Couldn't get bearer token"));

                fillSecurityContext(token);
                addAccessTokenHeader(token, response);

            } catch (FeignException | RestException e) {
                handleAuthenticationFailure(response, e);
            }
        }
        chain.doFilter(request, response);
    }

    private void addAccessTokenHeader(BearerTokenTO token, HttpServletResponse response) {
        response.setHeader(ACCESS_TOKEN, token.getAccess_token());
    }
}

