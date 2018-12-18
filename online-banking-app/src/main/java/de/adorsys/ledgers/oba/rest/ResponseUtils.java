package de.adorsys.ledgers.oba.rest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import de.adorsys.ledgers.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.ledgers.oba.domain.AuthorizeResponse;

@Service
public class ResponseUtils {

    private static final String CONSENT_COOKIE_NAME = "CONSENT";
	private static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
	
	private static final String UNKNOWN_CREDENTIALS = "Unknown credentials";
	
//	@Value("${online.banking.https.enabled:false}")
	private static final boolean https_enabled = false;

	public ResponseUtils() {
//		this.https_enabled = https_enabled;
	}

	/*
	 * Set both access token cookie and consent cookie.
	 * 
	 * @param response
	 * @param consentReference
	 * @param accessTokenString
	 * @param accessTokenTO
	 */
	public void setCookies(HttpServletResponse response, ConsentReference consentReference, String accessTokenString,
			AccessTokenTO accessTokenTO) {

		int validity = 300;// default to five seconds.
	    if(StringUtils.isNoneBlank(accessTokenString) && accessTokenTO!=null) {
	    	long diffInMillies = Math.abs(new Date().getTime() - accessTokenTO.getExp().getTime());
	    	validity = ((Long)TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS)).intValue();
			// Set Cookie. Access Token
			Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessTokenString);
			accessTokenCookie.setHttpOnly(true);
			accessTokenCookie.setSecure(https_enabled);
			accessTokenCookie.setMaxAge(validity);
			response.addCookie(accessTokenCookie);
	    }
		
	    if(StringUtils.isNoneBlank(accessTokenString)) {
			// Set cookie consent
			Cookie consentCookie = new Cookie(CONSENT_COOKIE_NAME, consentReference.getConsentCookie());
			consentCookie.setHttpOnly(true);
			consentCookie.setSecure(https_enabled);
			consentCookie.setMaxAge(validity);
			response.addCookie(consentCookie);
	    }
	}

	
	public ResponseEntity<AuthorizeResponse> respond(AuthorizeResponse authResponse, String message) {
		authResponse.setUserMessage(message);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponse);
	}

	public ResponseEntity<AuthorizeResponse> unknownCredentials(AuthorizeResponse authResponse) {
		authResponse.setUserMessage(UNKNOWN_CREDENTIALS);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(authResponse);
	}
}
