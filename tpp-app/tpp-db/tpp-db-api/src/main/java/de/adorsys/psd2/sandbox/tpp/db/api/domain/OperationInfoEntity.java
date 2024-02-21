/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

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
