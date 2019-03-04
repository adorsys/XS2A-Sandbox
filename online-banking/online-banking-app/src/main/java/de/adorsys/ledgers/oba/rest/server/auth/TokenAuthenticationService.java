package de.adorsys.ledgers.oba.rest.server.auth;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
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
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;

@Service
public class TokenAuthenticationService {
    private final Logger logger = LoggerFactory.getLogger(TokenAuthenticationService.class);
    
    private final UserMgmtRestClient ledgersUserMgmt;
	private final AuthRequestInterceptor authInterceptor;

	public TokenAuthenticationService(UserMgmtRestClient ledgersUserMgmt, AuthRequestInterceptor authInterceptor) {
		super();
		this.ledgersUserMgmt = ledgersUserMgmt;
		this.authInterceptor = authInterceptor;
	}
	private String readAccessTokenCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if(cookies==null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if("ACCESS_TOKEN".equalsIgnoreCase(cookie.getName())){
				return cookie.getValue();
			}
		}
		return null;
	}
	public Authentication getAuthentication(HttpServletRequest request) {
		String accessToken = readAccessTokenCookie(request);
        if(StringUtils.isBlank(accessToken)) {
            debug(String.format("Missing cookie with name %s.", "ACCESS_TOKEN"));
            return null;
        }
        BearerTokenTO bearerToken = null;
        try {
			authInterceptor.setAccessToken(accessToken);
        	ResponseEntity<BearerTokenTO> responseEntity = ledgersUserMgmt.validate(accessToken);
        	if(responseEntity.getStatusCode()==HttpStatus.OK) {
        		bearerToken = responseEntity.getBody();
        	}
        } finally {
			authInterceptor.setAccessToken(null);
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
