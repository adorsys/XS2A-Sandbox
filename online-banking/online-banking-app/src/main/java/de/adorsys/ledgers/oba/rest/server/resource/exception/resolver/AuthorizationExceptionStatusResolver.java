package de.adorsys.ledgers.oba.rest.server.resource.exception.resolver;

import de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

import static de.adorsys.ledgers.oba.service.api.domain.exception.AuthErrorCode.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AuthorizationExceptionStatusResolver {
    private static final Map<AuthErrorCode, HttpStatus> container = new EnumMap<>(AuthErrorCode.class);

    static {
        //403 Block
        container.put(LOGIN_FAILED, FORBIDDEN);
        container.put(ACCESS_FORBIDDEN, FORBIDDEN);
        container.put(AUTHENTICATION_FAILED, FORBIDDEN);
        container.put(CONSENT_DATA_UPDATE_FAILED, INTERNAL_SERVER_ERROR);
    }

    private AuthorizationExceptionStatusResolver() {
    }

    public static HttpStatus resolveHttpStatusByCode(AuthErrorCode code) {
        return container.get(code);
    }
}
