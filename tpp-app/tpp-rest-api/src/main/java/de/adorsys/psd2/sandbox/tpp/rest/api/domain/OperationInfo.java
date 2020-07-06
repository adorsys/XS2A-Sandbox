package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationInfo {
    private long id;
    private OperationType operationType;
    /**
     * Encrypted consent/payment id
     */
    private String encryptedOperationId;
    /**
     * branchId in Ledgers
     */
    private String tppId;
    private LocalDateTime created;
}
