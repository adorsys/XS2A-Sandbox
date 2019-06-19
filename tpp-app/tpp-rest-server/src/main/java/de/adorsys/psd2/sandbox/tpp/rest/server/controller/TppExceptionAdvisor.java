package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.server.exception.ErrorResponse;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice(basePackages = "de.adorsys.psd2.sandbox.tpp.rest.server.controller")
public class TppExceptionAdvisor {

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map> handleException(Exception ex, HandlerMethod handlerMethod) {
        log.warn("Exception handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(buildContentMap(500, ex.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TppException.class)
    public ResponseEntity<Map> handleTppException(TppException ex, HandlerMethod handlerMethod) {
        log.warn("TppException handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        Map<String, String> body = buildContentMap(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.getCode()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map> handleFeignException(FeignException ex, HandlerMethod handlerMethod) {
        log.warn("FeignException handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        Map<String, String> body = buildContentMap(ex.status(), ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.status()));
    }

    private Map<String, String> buildContentMap(int code, String message) {
        return new ErrorResponse()
                   .buildContent(code, message);
    }
}
