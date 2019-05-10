package de.adorsys.ledgers.oba.rest.api.consentref;

public class ConsentReference {

    private String authorizationId;
    private String redirectId;
    private ConsentType consentType;
    private String encryptedConsentId;
    private String cookieString;

    public String getAuthorizationId() {
        return authorizationId;
    }
    public void setAuthorizationId(String authorizationId) {
        this.authorizationId = authorizationId;
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
