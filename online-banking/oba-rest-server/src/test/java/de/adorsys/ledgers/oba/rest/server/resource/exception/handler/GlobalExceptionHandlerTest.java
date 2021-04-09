package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.ledgers.oba.rest.server.resource.ResponseUtils;
import de.adorsys.ledgers.oba.rest.server.resource.exception.resolver.AisExceptionStatusResolver;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler service;

    @Mock
    private ResponseUtils responseUtils;
    @Mock
    private HttpServletResponse response;
    @Spy
    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @Test
    void handler_coverage() {
        boolean allMatch = Arrays.stream(ObaErrorCode.values())
                               .map(c -> {
                                   HttpStatus status = AisExceptionStatusResolver.resolveHttpStatusByCode(c);
                                   if (status == null) {
                                       log.error("ErrorCode {} not covered with resolver!", c.name());
                                   }
                                   return status;
                               })
                               .allMatch(Objects::nonNull);
        assertTrue(allMatch);
    }

    @Test
    void handleAisException() {
        // When
        ResponseEntity<Map<String, String>> result = service.handleAisException(ObaException.builder().devMessage("Msg").obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build());

        // Then
        compareBodies(result, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400, "Msg")));
    }

    @Test
    void handleFeignException() throws JsonProcessingException, NoSuchMethodException, NoSuchFieldException {
        // When
        ResponseEntity<Map<String, String>> result = service.handleFeignException(FeignException.errorStatus("method", getResponse()), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "[401 Msg] during [POST] to [] [method]: [\"e2Rldk1lc3NhZ2U9TXNnfQ==\"]"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleFeignException_couldNotReadJsonContent() throws NoSuchMethodException {
        //Given
        Response response = Response.builder()
                                .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, new RequestTemplate()))
                                .reason("Msg")
                                .headers(new HashMap<>())
                                .status(401)
                                .body("WRONG_JSON", StandardCharsets.UTF_8)
                                .build();
        // When
        ResponseEntity<Map<String, String>> result = service.handleFeignException(FeignException.errorStatus("method", response), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "[401 Msg] during [POST] to [] [method]: [WRONG_JSON]"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleFeignException_IllegalArgumentException() throws JsonProcessingException, NoSuchMethodException {
        // Given
        Response response = Response.builder()
                                .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, new RequestTemplate()))
                                .reason("Msg")
                                .headers(new HashMap<>())
                                .status(123)
                                .body(mapper.writeValueAsString(Map.of("devMessage", "Msg").toString().getBytes()).getBytes())
                                .build();

        // When
        ResponseEntity<Map<String, String>> result = service.handleFeignException(FeignException.errorStatus("method", response), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(getExpected(123, "[123 Msg] during [POST] to [] [method]: [\"e2Rldk1lc3NhZ2U9TXNnfQ==\"]"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleException() throws NoSuchMethodException {
        // When
        ResponseEntity<Map<String, String>> actual = service.handleException(new RuntimeException("message"), new HandlerMethod(service, "toString", null));

        // Then
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getExpected(HttpStatus.INTERNAL_SERVER_ERROR.value(), "message"));
        compareBodies(actual, expected);
    }

    @Test
    void handleAccessDeniedException() {
        ResponseEntity<Map<String, String>> actual = service.handleAccessDeniedException(new AccessDeniedException("message"));

        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "message"));
        compareBodies(actual, expected);
    }

    @Test
    void handleAisException_cookieError() {
        ResponseEntity<Map<String, String>> result = service.handleAisException(ObaException.builder()
                                                                                    .devMessage("Msg")
                                                                                    .obaErrorCode(ObaErrorCode.COOKIE_ERROR)
                                                                                    .build());

        verify(responseUtils, times(1)).removeCookies(response);
        compareBodies(result, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400, "Msg")));
    }

    @Test
    void handleAuthException() {
        ResponseEntity<Map<String, String>> actual = service.handleAuthException(new ConnectException("message"));

        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getExpected(500, "message"));
        compareBodies(actual, expected);
    }

    private Response getResponse() throws JsonProcessingException {
        return Response.builder()
                   .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, new RequestTemplate()))
                   .reason("Msg")
                   .headers(new HashMap<>())
                   .status(401)
                   .body(mapper.writeValueAsString(Map.of("devMessage", "Msg").toString().getBytes()).getBytes())
                   .build();
    }

    private void compareBodies(ResponseEntity<Map<String, String>> result, ResponseEntity<Map<String, String>> expected) {
        assertNotNull(result);
        assertEquals(expected.getStatusCode(), result.getStatusCode());
        Map<String, String> resultBody = result.getBody();
        Map<String, String> expectedBody = expected.getBody();
        assertNotNull(resultBody);
        assertNotNull(expectedBody);
        assertThat(resultBody).containsEntry("code", expectedBody.get("code"));
        assertThat(resultBody).containsEntry("message", expectedBody.get("message"));
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
