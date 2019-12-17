package de.adorsys.ledgers.oba.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.oauth.OauthServerInfoTO;
import de.adorsys.ledgers.oba.service.impl.service.OauthServerLinkResolver;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OauthServerLinkResolverTest {
    private final String PMT_ID = "pmt1";
    private final String AUTH_ID = "12345";

    private String obaFeBaseUri = "http://localhost:4400";
    private String obaBeBaseUri = "http://localhost:8090";

    @Test
    public void resolve_parametrized() {
        OauthServerLinkResolver resolver = new OauthServerLinkResolver(new OauthServerInfoTO(), PMT_ID, null, null, AUTH_ID, obaBeBaseUri, obaFeBaseUri);
        OauthServerInfoTO result = resolver.resolve();

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getParametrizedResponse());
    }

    @Test
    public void resolve_non_parametrized() {
        OauthServerLinkResolver resolver = new OauthServerLinkResolver(new OauthServerInfoTO(), null, null, null, null, obaBeBaseUri, obaFeBaseUri);
        OauthServerInfoTO result = resolver.resolve();

        assertThat(result).isEqualToComparingFieldByFieldRecursively(getNonParametrizedResponse());
    }

    private OauthServerInfoTO getParametrizedResponse() {
        return new OauthServerInfoTO("http://localhost:4400/payment-initiation/login?redirectId=12345&paymentId=pmt1&oauth2=true", "http://localhost:8090/oauth/token", null, null);
    }

    private OauthServerInfoTO getNonParametrizedResponse() {
        return new OauthServerInfoTO("http://localhost:4400/auth/authorize?redirect_uri=", "http://localhost:8090/oauth/token", null, null);
    }
}
