package de.adorsys.ledgers.oba.rest.server.resource;

import java.net.HttpCookie;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.ledgers.oba.rest.api.domain.OnlineBankingResponse;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.rest.api.domain.PsuMessageCategory;
import de.adorsys.ledgers.oba.rest.api.domain.ValidationCode;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;

@Service
public class ResponseUtils {
	private static final String LOCATION_HEADER_NAME = "Location";
	public static final String CONSENT_COOKIE_NAME = "CONSENT";
	public static final String ACCESS_TOKEN_COOKIE_NAME = "ACCESS_TOKEN";
	private static final String TOKEN_PREFIX = "Bearer ";

	public static final String UNKNOWN_CREDENTIALS = "Unknown credentials";
	public static final String REQUEST_WITH_REDIRECT_NOT_FOUND = "Request with redirect id not found";

//	@Value("${online.banking.https.enabled:false}")
	private static final boolean https_enabled = false;

	public ResponseUtils() {
//		this.https_enabled = https_enabled;
	}

	/*
	 * Set both access token cookie and consent cookie.
	 * 
	 * @param response
	 * 
	 * @param consentReference
	 * 
	 * @param accessTokenString
	 * 
	 * @param accessTokenTO
	 */
	public void setCookies(HttpServletResponse response, ConsentReference consentReference, String accessTokenString,
			AccessTokenTO accessTokenTO) {

		int validity = 300;// default to five seconds.
		if (StringUtils.isNoneBlank(accessTokenString) && accessTokenTO != null) {
			long diffInMillies = Math.abs(new Date().getTime() - accessTokenTO.getExp().getTime());
			validity = ((Long) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS)).intValue();
			// Set Cookie. Access Token
			Cookie accessTokenCookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, accessTokenString);
			accessTokenCookie.setHttpOnly(true);
			accessTokenCookie.setSecure(https_enabled);
			accessTokenCookie.setMaxAge(validity);
			response.addCookie(accessTokenCookie);
		} else {
			removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
		}

		if (StringUtils.isNoneBlank(consentReference.getCookieString())) {
			// Set cookie consent
			Cookie consentCookie = new Cookie(CONSENT_COOKIE_NAME, consentReference.getCookieString());
			consentCookie.setHttpOnly(true);
			consentCookie.setSecure(https_enabled);
			consentCookie.setMaxAge(validity);
			response.addCookie(consentCookie);
		}
	}

	public void removeCookies(HttpServletResponse response) {
		removeCookie(response, ACCESS_TOKEN_COOKIE_NAME);
		removeCookie(response, CONSENT_COOKIE_NAME);
	}

	private void removeCookie(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, "");
		cookie.setHttpOnly(true);
		cookie.setSecure(https_enabled);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> forbidden(T authResp, String message, HttpServletResponse httpResp) {
		return error(authResp, HttpStatus.FORBIDDEN, message, httpResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> unknownCredentials(
			T resp, HttpServletResponse httpResp) {
		return error(resp, HttpStatus.FORBIDDEN, UNKNOWN_CREDENTIALS, httpResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> requestWithRedNotFound(T authResp, HttpServletResponse httpResp) {
		return error(authResp, HttpStatus.NOT_FOUND, REQUEST_WITH_REDIRECT_NOT_FOUND, httpResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> couldNotProcessRequest(T authResp, 
			HttpStatus status, HttpServletResponse httpResp) {
		return couldNotProcessRequest(authResp, "Could not process request. See status code.", status, httpResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> couldNotProcessRequest(T authResp, String message,
			HttpStatus status, HttpServletResponse httpResp) {
		return error(authResp, status, message, httpResp);
	}
	
	public String authHeader(MiddlewareAuthentication auth) {
		return TOKEN_PREFIX + auth.getBearerToken().getAccess_token();
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> redirect(String locationURI, HttpServletResponse httpResp) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(LOCATION_HEADER_NAME, locationURI);
		removeCookies(httpResp);
		return new ResponseEntity<T>(headers, HttpStatus.FOUND);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> backToSender(T authResp, String tppNokRedirectUri, String tppOkRedirectUri,
			HttpServletResponse httpResp, HttpStatus originalStatus, ValidationCode validationCode) {
		String locationUri = StringUtils.isNotBlank(tppNokRedirectUri)
				? tppNokRedirectUri
						: tppOkRedirectUri;
		if(StringUtils.isBlank(locationUri)) {
			return couldNotProcessRequest(authResp, "Missing tpp redirect uri.", HttpStatus.BAD_REQUEST, httpResp);
		}
		String uriString = UriComponentsBuilder.fromUriString(locationUri).queryParam("VALIDATION_CODE", validationCode.name()).build().toUriString();
		return redirect(uriString, httpResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> error(T authResp, HttpStatus status, String message, HttpServletResponse response) {
		PsuMessage psuMessage = new PsuMessage();
		psuMessage.setCategory(PsuMessageCategory.ERROR);
		psuMessage.setText(message);
		psuMessage.setCode(status.toString());
		authResp.getPsuMessages().add(psuMessage);
		removeCookies(response);
		return ResponseEntity.status(status).body(authResp);
	}

	public <T extends OnlineBankingResponse> ResponseEntity<T> badRequest(T authResp, String message, HttpServletResponse httpResp) {
		return error(authResp, HttpStatus.BAD_REQUEST, message, httpResp);
	}
	
	public String consentCookie(String cookieString) {
		return cookie(cookieString, CONSENT_COOKIE_NAME);
	}

	public String accessTokenCookie(String cookieString) {
		return cookie(cookieString, ACCESS_TOKEN_COOKIE_NAME);
	}

	private String cookie(String cookieString, String name) {
		List<HttpCookie> cookies = HttpCookie.parse(cookieString);		
		for (HttpCookie httpCookie : cookies) {
			if(StringUtils.equalsIgnoreCase(httpCookie.getName(), name)){
				return httpCookie.getValue();
			}
		}
		return null;
	}
}
