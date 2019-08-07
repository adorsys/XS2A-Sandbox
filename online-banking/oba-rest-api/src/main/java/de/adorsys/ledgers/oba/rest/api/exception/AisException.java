package de.adorsys.ledgers.oba.rest.api.exception;

import de.adorsys.ledgers.oba.rest.api.domain.AisErrorCode;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AisException extends RuntimeException {
    private String devMessage;
    private AisErrorCode aisErrorCode;
}
