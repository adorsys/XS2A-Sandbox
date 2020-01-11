package de.adorsys.ledgers.oba.service.api.domain.exception;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Builder
@Data
public class ObaException extends RuntimeException {
    private final String devMessage;
    private final ObaErrorCode obaErrorCode;
}
