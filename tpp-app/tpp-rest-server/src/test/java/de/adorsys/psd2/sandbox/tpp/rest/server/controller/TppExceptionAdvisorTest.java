/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import feign.FeignException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.method.HandlerMethod;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TppExceptionAdvisorTest {
    private static final ObjectMapper STATIC_MAPPER = new ObjectMapper().findAndRegisterModules().registerModule(new JavaTimeModule());

    @InjectMocks
    private TppExceptionAdvisor service;

    @Test
    void handleException() throws NoSuchMethodException {
        // When
        ResponseEntity<Map> result = service.handleException(new Exception("Msg"), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getExpected(500, "Msg"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleTppException() throws NoSuchMethodException {
        // When
        ResponseEntity<Map> result = service.handleTppException(new TppException("Msg", 500), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getExpected(500, "Msg"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleFeignException() throws NoSuchMethodException, JsonProcessingException, NoSuchFieldException {
        // Given
        ReflectionTestUtils.setField(service, "objectMapper", STATIC_MAPPER);

        // When
        ResponseEntity<Map> result = service.handleFeignException(FeignException.errorStatus("method", getResponse()), new HandlerMethod(service, "toString", null));
        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(getExpected(401, "[401 Msg] during [POST] to [] [method]: [\"e2Rldk1lc3NhZ2U9TXNnfQ==\"]"));

        // Then
        compareBodies(result, expected);
    }

    @Test
    void handleIllegalArgumentException() throws NoSuchMethodException {
        //When
        ResponseEntity<Map> result = service.handleIllegalArgumentException(new IllegalArgumentException("Msg"), new HandlerMethod(service, "toString", null));

        ResponseEntity<Map<String, String>> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getExpected(400, "Msg"));

        // Then
        compareBodies(result, expected);
    }

    private Response getResponse() throws JsonProcessingException {
        return Response.builder()
                   .request(Request.create(Request.HttpMethod.POST, "", new HashMap<>(), null, Charset.defaultCharset()))
                   .reason("Msg")
                   .headers(new HashMap<>())
                   .status(401)
                   .body(STATIC_MAPPER.writeValueAsString(Map.of("devMessage", "Msg").toString().getBytes()).getBytes())
                   .build();
    }

    private void compareBodies(ResponseEntity<Map> result, ResponseEntity<Map<String, String>> expected) {
        assertNotNull(result);
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
