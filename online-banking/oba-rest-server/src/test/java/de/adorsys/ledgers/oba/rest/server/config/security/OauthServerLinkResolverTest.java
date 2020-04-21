package de.adorsys.ledgers.oba.rest.server.config.security;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OauthServerLinkResolverTest {
    private final String PMT_ID = "pmt1";
    private final String AUTH_ID = "12345";

    private String obaFeBaseUri = "http://localhost:4400";
    private String obaBeBaseUri = "http://localhost:8090";

    @Test
    void resolve_parametrized() {
        // Given
        OauthServerLinkResolver resolver = new OauthServerLinkResolver(new OauthServerInfoTO(), PMT_ID, null, null, AUTH_ID, obaBeBaseUri, obaFeBaseUri);
        OauthServerInfoTO result = resolver.resolve();

        // Then
        assertEquals(getParametrizedResponse(), result);
    }

    @Test
    void resolve_non_parametrized() {
        // Given
        OauthServerLinkResolver resolver = new OauthServerLinkResolver(new OauthServerInfoTO(), null, null, null, null, obaBeBaseUri, obaFeBaseUri);
        OauthServerInfoTO result = resolver.resolve();

        // Then
        assertEquals(getNonParametrizedResponse(), result);
    }

    private OauthServerInfoTO getParametrizedResponse() {
        return new OauthServerInfoTO("http://localhost:4400/payment-initiation/login?redirectId=12345&paymentId=pmt1&oauth2=true", "http://localhost:8090/oauth/token", null, null);
    }

    private OauthServerInfoTO getNonParametrizedResponse() {
        return new OauthServerInfoTO("http://localhost:4400/auth/authorize?redirect_uri=", "http://localhost:8090/oauth/token", null, null);
    }
}
