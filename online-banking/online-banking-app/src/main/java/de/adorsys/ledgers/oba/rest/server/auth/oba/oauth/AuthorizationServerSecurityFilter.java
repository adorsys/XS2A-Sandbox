package de.adorsys.ledgers.oba.rest.server.auth.oba.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.middleware.client.rest.OauthRestClient;
import de.adorsys.ledgers.oba.rest.server.auth.oba.AbstractAuthFilter;
import de.adorsys.ledgers.oba.rest.server.service.OauthServerLinkResolver;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
public class AuthorizationServerSecurityFilter extends AbstractAuthFilter {
    private final ObjectMapper mapper;
    private final OauthRestClient oauthRestClient;
    private final String obaFeBaseUri;
    private final String obaBeBaseUri;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        String redirectId = request.getParameter("redirectId");
        String paymentId = request.getParameter("paymentId");
        String consentId = request.getParameter("consentId");
        String cancellationId = request.getParameter("cancellationId");
        try {
            OauthServerInfoTO serverInfo = new OauthServerLinkResolver(oauthRestClient.oauthServerInfo().getBody(), paymentId, consentId, cancellationId, redirectId, obaBeBaseUri, obaFeBaseUri).resolve();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().print(mapper.writeValueAsString(serverInfo));
        } catch (FeignException e) {
            handleAuthenticationFailure(response, "Couldn't get oauth server info");
        }
    }
}
