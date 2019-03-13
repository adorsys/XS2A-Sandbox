package de.adorsys.ledgers.oba.rest.server.resource;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.rest.server.auth.MiddlewareAuthentication;

public class AuthUtils {

	public static boolean success(ResponseEntity<SCALoginResponseTO> authoriseForConsent) {
		// Success if there is a bearer token.
        return authoriseForConsent!=null && authoriseForConsent.getBody()!=null && authoriseForConsent.getBody().getBearerToken()!=null;
	}

	public static String psuId(MiddlewareAuthentication auth) {
		if(auth==null) {
			return null;
		}
		return psuId(auth.getBearerToken());
	}
	
	public static String psuId(BearerTokenTO bearerToken) {
		return bearerToken.getAccessTokenObject().getLogin();
	}
	
}
