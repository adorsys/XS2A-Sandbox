package de.adorsys.ledgers.oba.rest.server.auth.oba.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthCodeResponseTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.oba.AbstractAuthFilter;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.*;

@RequiredArgsConstructor
public class OauthCodeSecurityFilter extends AbstractAuthFilter {
    private final ObjectMapper mapper;
    private final OauthRestClient oauthRestClient;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String login = obtainFromHeader(request, USER_LOGIN);
        String pin = obtainFromHeader(request, USER_PIN);
        String redirectUrl = obtainFromRequest(request, REDIRECT_URI);

        if (StringUtils.isBlank(login) || StringUtils.isBlank(pin) || StringUtils.isBlank(redirectUrl)) {
            handleAuthenticationFailure(response, "Invalid credentials");
            return;
        }
        try {
            ResponseEntity<OauthCodeResponseTO> oauthCodeResponse = oauthRestClient.oauthCode(login, pin, redirectUrl);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(mapper.writeValueAsString(oauthCodeResponse.getBody()));
        } catch (FeignException e) {
            handleAuthenticationFailure(response, "Couldn't get oauth code");
        }
    }
}

