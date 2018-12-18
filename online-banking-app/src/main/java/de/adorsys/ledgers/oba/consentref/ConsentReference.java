package de.adorsys.ledgers.oba.consentref;

public class ConsentReference {
	
	private String scaId;
	private String consentCookie;
	private String consentId;
	public String getScaId() {
		return scaId;
	}
	public void setScaId(String scaId) {
		this.scaId = scaId;
	}
	public String getConsentCookie() {
		return consentCookie;
	}
	public void setConsentCookie(String consentCookie) {
		this.consentCookie = consentCookie;
	}
	public String getConsentId() {
		return consentId;
	}
	public void setConsentId(String consentId) {
		this.consentId = consentId;
	}
}
