package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import de.adorsys.ledgers.oba.rest.api.exception.AisException;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.AisExceptionStatusResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice()
public class GlobalExceptionHandler {
    private static final String MESSAGE = "message";
    private static final String DEV_MESSAGE = "devMessage";
    private static final String CODE = "code";
    private static final String DATE_TIME = "dateTime";

    @ExceptionHandler(AisException.class)
    public ResponseEntity<Map> handleAisException(AisException e) {
        HttpStatus status = AisExceptionStatusResolver.resolveHttpStatusByCode(e.getAisErrorCode());
        Map message = getHandlerContent(status, e.getDevMessage(), e.getDevMessage());
        return ResponseEntity.status(status).body(message);
    }

    private Map<String, String> getHandlerContent(HttpStatus status, String message, String devMessage) {
        Map<String, String> error = new HashMap<>();
        error.put(CODE, String.valueOf(status.value()));
        error.put(MESSAGE, message);
        error.put(DEV_MESSAGE, devMessage);
        error.put(DATE_TIME, LocalDateTime.now().toString());
        return error;
    }
}
