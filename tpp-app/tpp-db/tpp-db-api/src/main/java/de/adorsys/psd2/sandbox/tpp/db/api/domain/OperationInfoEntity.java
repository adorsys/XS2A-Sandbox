package de.adorsys.psd2.sandbox.tpp.db.api.domain;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "operation_info")
public class OperationInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "operation_info_generator")
    @SequenceGenerator(name = "operation_info_generator", sequenceName = "operation_info_id_seq", allocationSize = 1)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "op_type", nullable = false)
    private OperationTypeEntity operationType;
    /**
     * Encrypted consent/payment id
     */
    @Column(name = "encrypted_op_id", nullable = false)
    private String encryptedOperationId;
    /**
     * branchId in Ledgers
     */
    @Column(name = "tpp_id", nullable = false)
    private String tppId;

    @CreationTimestamp
    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
