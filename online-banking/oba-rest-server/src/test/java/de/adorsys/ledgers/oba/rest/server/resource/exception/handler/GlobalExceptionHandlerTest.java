package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.oba.rest.api.resource.exception.PaymentAuthorizeException;
import de.adorsys.ledgers.oba.service.api.domain.PaymentAuthorizeResponse;
import de.adorsys.ledgers.oba.service.api.domain.PsuMessage;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.AuthorizationException;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerTest {
    private final ObjectMapper STATIC_MAPPER = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @InjectMocks
    private GlobalExceptionHandler service;

    @Test
    public void handleAisException() {
        Whitebox.setInternalState(service, "objectMapper", STATIC_MAPPER);
        ResponseEntity<Map> result = service.handleAisException(ObaException.builder().devMessage("Msg").obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build());
        compareBodies(result, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400,"Msg")));
    }

    @Test
    public void handlePaymentAuthorizeException() {
        Whitebox.setInternalState(service, "objectMapper", STATIC_MAPPER);
        PaymentAuthorizeResponse authorizeResponse = new PaymentAuthorizeResponse(new PaymentTO());
        PsuMessage message = new PsuMessage();
        message.setCode("400");
        message.setText("Msg");
        authorizeResponse.setPsuMessages(List.of(message));
        ResponseEntity<Map> result = service.handlePaymentAuthorizeException(new PaymentAuthorizeException(ResponseEntity.status(HttpStatus.NOT_FOUND).body(authorizeResponse)));
        compareBodies(result, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400,"Msg")));
    }

    @Test
    public void handleAuthException() {
        Whitebox.setInternalState(service, "objectMapper", STATIC_MAPPER);
        ResponseEntity<Map> result = service.handleAuthException(AuthorizationException.builder().devMessage("Msg").errorCode(AuthErrorCode.ACCESS_FORBIDDEN).build());
        compareBodies(result, ResponseEntity.status(HttpStatus.FORBIDDEN).body(getExpected(403,"Msg")));
    }

    @Test
    public void handleFeignException() throws JsonProcessingException, NoSuchMethodException {
        Whitebox.setInternalState(service, "objectMapper", STATIC_MAPPER);
        ResponseEntity<Map> result = service.handleFeignException(FeignException.errorStatus("method", getResponse()), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "status 401 reading method"));
        compareBodies(result, expected);
    }


    private Response getResponse() throws JsonProcessingException {
        return Response.builder()
                   .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null))
                   .reason("Msg")
                   .headers(new HashMap<>())
                   .status(401)
                   .body(STATIC_MAPPER.writeValueAsString(Map.of("devMessage", "Msg").toString().getBytes()).getBytes())
                   .build();
    }

    private void compareBodies(ResponseEntity<Map> result, ResponseEntity<Map<String, String>> expected) {
        assertThat(result).isNotNull();
        assertThat(result.getStatusCode()).isEqualTo(expected.getStatusCode());
        Map<String, String> resultBody = result.getBody();
        Map<String, String> expectedBody = expected.getBody();
        assertThat(resultBody.get("code")).isEqualTo(expectedBody.get("code"));
        assertThat(resultBody.get("message")).isEqualTo(expectedBody.get("message"));
        assertThat(LocalDateTime.parse(resultBody.get("dateTime"))).isEqualToIgnoringSeconds(LocalDateTime.now());
    }

    private Map<String, String> getExpected(int code, String msg) {
        Map<String, String> map = new HashMap<>();
        map.put("dateTime", LocalDateTime.now().toString());
        map.put("code", String.valueOf(code));
        map.put("message", msg);
        return map;
    }
}
