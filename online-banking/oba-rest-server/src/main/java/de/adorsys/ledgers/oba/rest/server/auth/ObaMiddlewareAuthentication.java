package de.adorsys.ledgers.oba.rest.server.auth;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;

public class ObaMiddlewareAuthentication extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -778888356552035882L;

    public ObaMiddlewareAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public ObaMiddlewareAuthentication(Object principal, BearerTokenTO credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }

    public BearerTokenTO getBearerToken() {
    	return (BearerTokenTO) getCredentials();
    }
}
