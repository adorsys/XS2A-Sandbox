package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import de.adorsys.ledgers.oba.rest.api.exception.AisException;
import de.adorsys.ledgers.oba.rest.server.auth.oba.ErrorResponse;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.AisExceptionStatusResolver;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final String DEV_MESSAGE = "devMessage";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(AisException.class)
    public ResponseEntity<Map> handleAisException(AisException e) {
        HttpStatus status = AisExceptionStatusResolver.resolveHttpStatusByCode(e.getAisErrorCode());
        Map message = buildContentMap(status.value(), e.getDevMessage());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map> handleFeignException(FeignException ex, HandlerMethod handlerMethod) {
        log.warn("FeignException handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        Map<String, String> body = buildContentMap(ex.status(), resolveErrorMessage(ex));
        return new ResponseEntity<>(body, HttpStatus.valueOf(ex.status()));
    }

    private Map<String, String> buildContentMap(int code, String message) {
        return new ErrorResponse()
                   .buildContent(code, message);
    }

    private String resolveErrorMessage(FeignException ex) {
        ObjectReader reader = objectMapper.readerFor(Map.class);
        String jsonContent = new String(ex.content(), Charset.defaultCharset());
        try {
            Map<String, String> map = reader.readValue(jsonContent);
            return map.getOrDefault(DEV_MESSAGE, ex.getMessage());
        } catch (IOException e) {
            log.warn("Couldn't read json content");
        }
        return ex.getMessage();
    }
}
