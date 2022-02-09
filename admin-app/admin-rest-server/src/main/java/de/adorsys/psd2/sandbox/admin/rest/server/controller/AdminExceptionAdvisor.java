/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.sandbox.admin.rest.server.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.sandbox.admin.rest.server.exception.ErrorResponse;
import de.adorsys.psd2.sandbox.admin.rest.server.exception.AdminException;
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
@RestControllerAdvice(basePackages = "de.adorsys.psd2.sandbox.admin.rest.server.controller")
public class AdminExceptionAdvisor {
    private static final String DEV_MESSAGE = "devMessage";

    private final ObjectMapper objectMapper;

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Map> handleException(Exception ex, HandlerMethod handlerMethod) {
        log.warn("Exception handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(buildContentMap(500, ex.getMessage()), INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AdminException.class)
    public ResponseEntity<Map> handleTppException(AdminException ex, HandlerMethod handlerMethod) {
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
        HttpStatus status = HttpStatus.valueOf(ex.status() == 0 ? 503 : ex.status());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map> handleIllegalArgumentException(IllegalArgumentException ex, HandlerMethod handlerMethod) {
        log.warn("IllegalArgumentException handled in service: {}, message: {}",
                 handlerMethod.getMethod().getDeclaringClass().getSimpleName(), ex.getMessage());

        Map<String, String> body = buildContentMap(400, ex.getMessage());
        HttpStatus status = HttpStatus.valueOf(400);
        return new ResponseEntity<>(body, status);
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
