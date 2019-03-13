package de.adorsys.ledgers.oba.rest.api.consentref;

public class InvalidConsentException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidConsentException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidConsentException(String message) {
		super(message);
	}

	public InvalidConsentException(Throwable cause) {
		super(cause);
	}
}
