package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.oba.rest.server.auth.oba.ErrorResponse;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.AisExceptionStatusResolver;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Optional;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final String DEV_MESSAGE = "devMessage";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ObaException.class)
    public ResponseEntity<Map<String, String>> handleAisException(ObaException e) {
        HttpStatus status = AisExceptionStatusResolver.resolveHttpStatusByCode(e.getObaErrorCode());
        Map<String, String> message = buildContentMap(status.value(), e.getDevMessage());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> handleFeignException(FeignException ex, HandlerMethod handlerMethod) {
        log.warn("FeignException handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        Map<String, String> body = buildContentMap(ex.status(), resolveErrorMessage(ex));
        HttpStatus status = HttpStatus.I_AM_A_TEAPOT;
        try {
            status = HttpStatus.valueOf(ex.status());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Map<String, String>> handleAuthException(ConnectException e) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, String> message = buildContentMap(status.value(), e.getMessage());
        return ResponseEntity.status(status).body(message);
    }

    private Map<String, String> buildContentMap(int code, String message) {
        return new ErrorResponse()
                   .buildContent(code, message);
    }

    private String resolveErrorMessage(FeignException ex) {
        return ex.responseBody()
                   .map(ByteBuffer::array)
                   .map(b -> extractMessage(ex, b))
                   .orElse(ex.getMessage());
    }

    private String extractMessage(FeignException ex, byte[] b) {
        try {
            return Optional.ofNullable(objectMapper.readTree(b).get(DEV_MESSAGE))
                       .map(JsonNode::asText)
                       .orElseGet(ex::getMessage);
        } catch (IOException e) {
            log.warn("Couldn't read json content");
            return ex.getMessage();
        }
    }
}
