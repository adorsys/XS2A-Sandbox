package de.adorsys.ledgers.oba.rest.server.auth.oba;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponse {
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DATE_TIME = "dateTime";

    public Map<String, String> buildContent(int code, String message) {
        Map<String, String> error = new HashMap<>();
        error.put(CODE, String.valueOf(code));
        error.put(MESSAGE, message);
        error.put(DATE_TIME, LocalDateTime.now().toString());
        return error;
    }
}
