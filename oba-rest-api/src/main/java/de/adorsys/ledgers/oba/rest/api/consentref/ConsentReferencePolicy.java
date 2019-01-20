package de.adorsys.ledgers.oba.rest.api.consentref;

/**
 * Defines how to reference a consent.
 * 
 * @author fpo
 *
 */
public interface ConsentReferencePolicy {
	
	/**
	 * Produces The consent reference based on the inbound URL.
	 * 
	 * The inbound url will contain either the ais consentId or the pis paymentId.
	 * @param redirectId: the redirect id. 
	 * @param consentType: the type of consent requested payment or account information
	 * @param consentId the original customer redirected url
	 * @return a holder of the consent references
	 * @throws InvalidConsentException the consent is not found of the scaId does not match the consent cookie.
	 */
	ConsentReference fromURL(String redirectId, ConsentType consentType, String consentId) throws InvalidConsentException;
	
	/**
	 * Produces a consent reference from a web request.
	 * 
	 * @param scaId : the scaId from the request url
	 * @param cookieString : the cookie string associated with the request.
	 * @return a holder of the consent references
	 * @throws InvalidConsentException the consent is not found of the scaId does not match the consent cookie.
	 */
	ConsentReference fromRequest(String scaId, String cookieString) throws InvalidConsentException;


}
