package de.adorsys.ledgers.oba.rest.api.resource.exception;

import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import org.springframework.http.ResponseEntity;

//TODO refactor THIS
public class PaymentAuthorizeException extends RuntimeException {
	private static final long serialVersionUID = 5719983070625127158L;
	private final ResponseEntity<PaymentAuthorizeResponse> error;

	public PaymentAuthorizeException(ResponseEntity<PaymentAuthorizeResponse> error) {
		this.error = error;
	}

	public ResponseEntity<PaymentAuthorizeResponse> getError() {
		return error;
	}
}
