package de.adorsys.ledgers.oba.consentref;

public class ConsentReference {
	
	private String scaId;
	private String redirectId;
	private ConsentType consentType;
	private String encryptedConsentId;
	private String cookieString;
	
	public String getScaId() {
		return scaId;
	}
	public void setScaId(String scaId) {
		this.scaId = scaId;
	}
	public String getRedirectId() {
		return redirectId;
	}
	public void setRedirectId(String redirectId) {
		this.redirectId = redirectId;
	}
	public ConsentType getConsentType() {
		return consentType;
	}
	public void setConsentType(ConsentType consentType) {
		this.consentType = consentType;
	}
	public String getEncryptedConsentId() {
		return encryptedConsentId;
	}
	public void setEncryptedConsentId(String encryptedConsentId) {
		this.encryptedConsentId = encryptedConsentId;
	}
	public String getCookieString() {
		return cookieString;
	}
	public void setCookieString(String cookieString) {
		this.cookieString = cookieString;
	}
}
