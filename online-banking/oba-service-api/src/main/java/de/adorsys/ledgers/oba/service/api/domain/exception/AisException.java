package de.adorsys.ledgers.oba.service.api.domain.exception;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AisException extends RuntimeException {
    private String devMessage;
    private AisErrorCode aisErrorCode;
}
