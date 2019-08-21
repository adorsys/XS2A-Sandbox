package de.adorsys.psd2.sandbox.tpp.rest.server.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class TppException extends RuntimeException {
    private int code;
    private String message;

    public TppException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }
}
