package de.adorsys.psd2.sandbox.tpp.rest.server.auth;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class MiddlewareAuthentication extends UsernamePasswordAuthenticationToken {

    public MiddlewareAuthentication(Object principal, BearerTokenTO credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
