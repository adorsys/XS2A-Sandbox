package de.adorsys.psd2.sandbox.admin.rest.server.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class AdminException extends RuntimeException {
    private int code;
    private String message;

    public AdminException(String message, int code) {
        super(message);
        this.message = message;
        this.code = code;
    }
}
