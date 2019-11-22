package de.adorsys.ledgers.oba.rest.api.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.PaymentAuthorizeResponse;

//TODO refactor THIS
public class PaymentAuthorizeException extends Exception {
	private static final long serialVersionUID = 5719983070625127158L;
	private final ResponseEntity<PaymentAuthorizeResponse> error;

	public PaymentAuthorizeException(ResponseEntity<PaymentAuthorizeResponse> error) {
		this.error = error;
	}

	public ResponseEntity<PaymentAuthorizeResponse> getError() {
		return error;
	}
}
