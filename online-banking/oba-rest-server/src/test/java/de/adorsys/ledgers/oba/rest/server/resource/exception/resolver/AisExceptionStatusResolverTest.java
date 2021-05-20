package de.adorsys.ledgers.oba.rest.server.resource.exception.resolver;

import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AisExceptionStatusResolverTest {

    @ParameterizedTest
    @MethodSource("testCases")
    void resolveHttpStatusByCode(ObaErrorCode errorCode, HttpStatus expected) {
        assertEquals(expected, AisExceptionStatusResolver.resolveHttpStatusByCode(errorCode));
        assertEquals(8, ObaErrorCode.values().length, "If fail add new test case.");
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
            Arguments.arguments(null, null),

            Arguments.arguments(ObaErrorCode.AIS_BAD_REQUEST, HttpStatus.BAD_REQUEST),

            Arguments.arguments(ObaErrorCode.ACCESS_FORBIDDEN, HttpStatus.FORBIDDEN),

            Arguments.arguments(ObaErrorCode.NOT_FOUND, HttpStatus.NOT_FOUND),

            Arguments.arguments(ObaErrorCode.CONNECTION_ERROR, HttpStatus.INTERNAL_SERVER_ERROR),
            Arguments.arguments(ObaErrorCode.CONVERSION_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR),

            Arguments.arguments(ObaErrorCode.AUTH_EXPIRED, HttpStatus.UNAUTHORIZED),
            Arguments.arguments(ObaErrorCode.LOGIN_FAILED, HttpStatus.UNAUTHORIZED),
            Arguments.arguments(ObaErrorCode.RESOURCE_EXPIRED, HttpStatus.GONE)
        );
    }
}
