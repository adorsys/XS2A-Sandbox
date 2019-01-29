package de.adorsys.ledgers.oba.rest.api.exception;

import org.springframework.http.ResponseEntity;

import de.adorsys.ledgers.oba.rest.api.domain.OnlineBankingResponse;

public class ConsentWorkflowException extends Exception {
	private static final long serialVersionUID = -5501213223443600273L;
	private final ResponseEntity<OnlineBankingResponse> error;

	public ConsentWorkflowException(ResponseEntity<OnlineBankingResponse> error) {
		this.error = error;
	}

	public ResponseEntity<OnlineBankingResponse> getError() {
		return error;
	}
}
