package de.adorsys.ledgers.oba.service.api.domain.exception;

public class InvalidConsentException extends RuntimeException {
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
