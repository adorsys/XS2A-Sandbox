package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.rest.server.auth.oba.ErrorResponse;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.ObaExceptionStatusResolver;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.AuthorizationExceptionStatusResolver;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.util.Map;

import static java.util.Optional.ofNullable;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final String DEV_MESSAGE = "devMessage";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(ObaException.class)
    public ResponseEntity<Map> handleAisException(ObaException e) {
        HttpStatus status = ObaExceptionStatusResolver.resolveHttpStatusByCode(e.getObaErrorCode());
        Map message = buildContentMap(status.value(), e.getDevMessage());
        return ResponseEntity.status(status).body(message);
    }

    @ExceptionHandler(PaymentAuthorizeException.class)
    public ResponseEntity<Map> handlePaymentAuthorizeException(PaymentAuthorizeException e) {
        PaymentAuthorizeResponse body = e.getError().getBody();
        PsuMessage psuMessage = body.getPsuMessages().get(0);
        Map content = buildContentMap(psuMessage.getCode(), psuMessage.getText());
        return new ResponseEntity<>(content, HttpStatus.valueOf(psuMessage.getCode()));
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<Map> handleAuthException(AuthorizationException e) {
        HttpStatus status = AuthorizationExceptionStatusResolver.resolveHttpStatusByCode(e.getErrorCode());
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

    private Map<String, String> buildContentMap(String code, String message) {
        return buildContentMap(Integer.parseInt(code), message);
    }

    private Map<String, String> buildContentMap(int code, String message) {
        return new ErrorResponse()
                   .buildContent(code, message);
    }

    private String resolveErrorMessage(FeignException ex) {
        try {
            return ex.content() != null
                       ? ofNullable(objectMapper.readTree(ex.content()).get(DEV_MESSAGE))
                             .map(JsonNode::asText)
                             .orElseGet(ex::getMessage)
                       : ex.getMessage();
        } catch (IOException e) {
            log.warn("Couldn't read json content");
        }
        return ex.getMessage();
    }
}
