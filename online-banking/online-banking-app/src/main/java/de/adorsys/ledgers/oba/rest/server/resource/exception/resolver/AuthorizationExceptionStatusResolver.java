package de.adorsys.ledgers.oba.rest.server.resource.exception.resolver;

import de.adorsys.ledgers.oba.rest.api.exception.AuthErrorCode;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

import static de.adorsys.ledgers.oba.rest.api.exception.AuthErrorCode.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;

public class AuthorizationExceptionStatusResolver {
    private static final Map<AuthErrorCode, HttpStatus> container = new EnumMap<>(AuthErrorCode.class);

    static {
        //403 Block
        container.put(LOGIN_FAILED, FORBIDDEN);
        container.put(ACCESS_FORBIDDEN, FORBIDDEN);
        container.put(AUTHENTICATION_FAILED, FORBIDDEN);
    }

    private AuthorizationExceptionStatusResolver() {
    }

    public static HttpStatus resolveHttpStatusByCode(AuthErrorCode code) {
        return container.get(code);
    }
}
