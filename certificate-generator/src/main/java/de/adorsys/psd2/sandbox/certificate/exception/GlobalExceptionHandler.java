package de.adorsys.psd2.sandbox.certificate.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MESSAGE = "message";
    private static final String DEV_MESSAGE = "devMessage";
    private static final String CODE = "code";
    private static final String DATE_TIME = "dateTime";

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<Map> handleAisException(InvalidFormatException e) {
        String messageException = "Invalid initial data";
        Map message = getHandlerContent(HttpStatus.BAD_REQUEST, messageException, messageException);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                   .body(message);
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
