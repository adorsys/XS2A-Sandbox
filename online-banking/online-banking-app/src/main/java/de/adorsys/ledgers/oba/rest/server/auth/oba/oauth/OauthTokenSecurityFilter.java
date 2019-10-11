package de.adorsys.ledgers.oba.rest.server.auth.oba.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.oauth.GrantTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.oba.AbstractAuthFilter;
import de.adorsys.ledgers.oba.rest.server.auth.oba.RestException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static de.adorsys.ledgers.oba.rest.server.auth.oba.SecurityConstant.OAUTH_CODE;

@RequiredArgsConstructor
public class OauthTokenSecurityFilter extends AbstractAuthFilter {
    private final ObjectMapper mapper;
    private final OauthRestClient oauthRestClient;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String oauthCode = obtainFromHeader(request, OAUTH_CODE);

        if (StringUtils.isBlank(oauthCode)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            ResponseEntity<BearerTokenTO> tokenResponse = oauthRestClient.oauthToken(GrantTypeTO.AUTHORISATION_CODE, oauthCode);
            BearerTokenTO bearerTokenTO = Optional.ofNullable(tokenResponse.getBody())
                                              .orElseThrow(() -> new RestException("Couldn't get bearer token"));

            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(mapper.writeValueAsString(bearerTokenTO));
        } catch (FeignException | RestException e) {
            handleAuthenticationFailure(response, "Couldn't get bearer token");
        }
    }
}

