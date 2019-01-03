package de.adorsys.ledgers.oba.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.domain.OnlineBankingResponse;

public class PaymentWorkflowException extends Exception {
	private static final long serialVersionUID = 6623407016540471912L;
	private final ResponseEntity<OnlineBankingResponse> error;

	public PaymentWorkflowException(ResponseEntity<OnlineBankingResponse> error) {
		this.error = error;
	}

	public ResponseEntity<OnlineBankingResponse> getError() {
		return error;
	}
}
