package de.adorsys.ledgers.oba.rest.api.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthorizationException extends RuntimeException{
    private String devMessage;
    private AuthErrorCode errorCode;
}
