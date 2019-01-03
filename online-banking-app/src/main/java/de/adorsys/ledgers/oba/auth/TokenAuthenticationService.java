package de.adorsys.ledgers.oba.auth;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;

@Service
public class TokenAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_KEY = "Authorization";
    
    private final UserMgmtRestClient ledgersUserMgmt;


	public TokenAuthenticationService(UserMgmtRestClient ledgersUserMgmt) {
		super();
		this.ledgersUserMgmt = ledgersUserMgmt;
	}

	public Authentication getAuthentication(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_KEY);
        if(StringUtils.isBlank(headerValue)) {
            debug(String.format("Header value '{}' is blank.", HEADER_KEY));
            return null;
        }

        // Accepts only Bearer token
        if(!StringUtils.startsWithIgnoreCase(headerValue, TOKEN_PREFIX)) {
            debug(String.format("Header value does not start with '$s'.", TOKEN_PREFIX));
            return null;
        }

        // Strip prefix
        String accessToken = StringUtils.substringAfterLast(headerValue, " ");

        BearerTokenTO bearerToken = null;
        ResponseEntity<BearerTokenTO> responseEntity = ledgersUserMgmt.validate(accessToken);
        if(responseEntity.getStatusCode()==HttpStatus.OK) {
        	bearerToken = responseEntity.getBody();
        }

        if (bearerToken==null) {
        	debug("Token is not valid.");
            return null;
        }

        // process roles
        AccessTokenTO token = bearerToken.getAccessTokenObject();
        List<GrantedAuthority> authorities = new ArrayList<>();
        if(token.getRole()!=null) {
        	authorities.add(new SimpleGrantedAuthority("ROLE_" + token.getRole().name()));
        }

        return new MiddlewareAuthentication(token.getSub(), bearerToken, authorities);
    }
	
	private void debug(String s) {
        if(logger.isDebugEnabled()) {
        	logger.debug(s);
        }
	}
}
