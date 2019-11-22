package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.ErrorResponse;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.util.Map;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "de.adorsys.psd2.sandbox.tpp.rest.server.controller")
public class TppExceptionAdvisor {
    private static final String DEV_MESSAGE = "devMessage";

    private final ObjectMapper objectMapper;

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

        Map<String, String> body = buildContentMap(ex.status(), resolveErrorMessage(ex));
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.status()));
    }

    private String resolveErrorMessage(FeignException ex) {
        try {
            if (ex.content() == null) {
                return ex.getMessage();
            }
            JsonNode tree = objectMapper.readTree(ex.content());
            return ofNullable(tree.get(DEV_MESSAGE))
                       .map(JsonNode::asText)
                       .orElseGet(() -> ofNullable(tree.get("message"))
                                            .map(JsonNode::asText)
                                            .orElse(ex.getMessage()));
        } catch (IOException e) {
            log.warn("Couldn't read json content");
        }
        return ex.getMessage();
    }

    private Map<String, String> buildContentMap(int code, String message) {
        return new ErrorResponse()
                   .buildContent(code, message);
    }
}
