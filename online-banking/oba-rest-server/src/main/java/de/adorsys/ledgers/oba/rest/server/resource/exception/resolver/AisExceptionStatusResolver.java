package de.adorsys.ledgers.oba.rest.server.resource.exception.resolver;

import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import org.springframework.http.HttpStatus;

import java.util.EnumMap;
import java.util.Map;

import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.AIS_BAD_REQUEST;
import static de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode.CONNECTION_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

public class AisExceptionStatusResolver {
    private static final Map<ObaErrorCode, HttpStatus> container = new EnumMap<>(ObaErrorCode.class);

    static {
        //400 Block
        container.put(AIS_BAD_REQUEST, BAD_REQUEST);

        //500 Block
        container.put(CONNECTION_ERROR, INTERNAL_SERVER_ERROR);
    }

    private AisExceptionStatusResolver() {
    }

    public static HttpStatus resolveHttpStatusByCode(ObaErrorCode code) {
        return container.get(code);
    }
}
