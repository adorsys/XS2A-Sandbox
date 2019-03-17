package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import org.apache.commons.lang3.StringUtils;

public class AuthUrl {
	private final String encryptedConsentId;
	private final String authorizationId;
	
	public AuthUrl(String encryptedConsentId, String authorizationId) {
		this.encryptedConsentId = encryptedConsentId;
		this.authorizationId = authorizationId;
	}
	public String getEncryptedConsentId() {
		return encryptedConsentId;
	}
	public String getAuthorizationId() {
		return authorizationId;
	}
	
	public static AuthUrl parse(String url) {
		String authorizationId = StringUtils.substringAfterLast(url, "authorisations/");
		String encryptedConsentId = StringUtils.substringBeforeLast(url, "/authorisations");
		encryptedConsentId = StringUtils.substringAfterLast(encryptedConsentId, "/");
		return new AuthUrl(encryptedConsentId, authorizationId);
	}
}
