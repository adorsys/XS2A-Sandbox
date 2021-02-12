package de.adorsys.ledgers.oba.rest.server.resource.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.method.HandlerMethod;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private final ObjectMapper STATIC_MAPPER = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @InjectMocks
    private GlobalExceptionHandler service;

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
    void handleAisException() throws NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("objectMapper"), STATIC_MAPPER);

        // When
        ResponseEntity<Map<String, String>> result = service.handleAisException(ObaException.builder().devMessage("Msg").obaErrorCode(ObaErrorCode.AIS_BAD_REQUEST).build());

        // Then
        compareBodies(result, ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400, "Msg")));
    }

    @Test
    void handleFeignException() throws JsonProcessingException, NoSuchMethodException, NoSuchFieldException {
        // Given
        FieldSetter.setField(service, service.getClass().getDeclaredField("objectMapper"), STATIC_MAPPER);

        // When
        ResponseEntity<Map<String, String>> result = service.handleFeignException(FeignException.errorStatus("method", getResponse()), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "[401 Msg] during [POST] to [] [method]: [\"e2Rldk1lc3NhZ2U9TXNnfQ==\"]"));

        // Then
        compareBodies(result, expected);
    }


    private Response getResponse() throws JsonProcessingException {
        return Response.builder()
                   .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, new RequestTemplate()))
                   .reason("Msg")
                   .headers(new HashMap<>())
                   .status(401)
                   .body(STATIC_MAPPER.writeValueAsString(Map.of("devMessage", "Msg").toString().getBytes()).getBytes())
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
