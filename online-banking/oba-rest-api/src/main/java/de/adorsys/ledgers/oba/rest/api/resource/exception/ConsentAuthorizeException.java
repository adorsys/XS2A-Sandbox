package de.adorsys.ledgers.oba.rest.api.resource.exception;

import de.adorsys.ledgers.oba.service.api.domain.ConsentAuthorizeResponse;
import org.springframework.http.ResponseEntity;

//TODO refactor THIS
public class ConsentAuthorizeException extends RuntimeException {
	private static final long serialVersionUID = 7876974990567439886L;
	private final ResponseEntity<ConsentAuthorizeResponse> error;

	public ConsentAuthorizeException(ResponseEntity<ConsentAuthorizeResponse> error) {
		this.error = error;
	}

	public ResponseEntity<ConsentAuthorizeResponse> getError() {
		return error;
	}
}
