package de.adorsys.ledgers.oba.consentref;

public class DefaultConsentReferencePolicy implements ConsentReferencePolicy {

	@Override
	public ConsentReference fromURL(ServicePort port, String consentId) throws InvalidConsentException {
		ConsentReference cr = new ConsentReference();
		cr.setConsentCookie(consentId);
		cr.setConsentId(consentId);
		cr.setScaId(consentId);
		return cr;
	}

	@Override
	public ConsentReference fromRequest(String scaId, String consentId) throws InvalidConsentException {
		ConsentReference cr = new ConsentReference();
		cr.setConsentCookie(consentId);
		cr.setConsentId(consentId);
		cr.setScaId(scaId);
		return cr;
	}

}
