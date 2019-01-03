package de.adorsys.ledgers.oba.exception;

public class CurrencyMismatchedException extends Exception {
	private static final long serialVersionUID = -3070407341002625728L;

	public CurrencyMismatchedException(String message) {
		super(message);
	}
}
