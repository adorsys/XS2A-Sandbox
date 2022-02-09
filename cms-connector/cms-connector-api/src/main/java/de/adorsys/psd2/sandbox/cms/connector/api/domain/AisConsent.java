/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.sandbox.cms.connector.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AisConsent {
    private PsuIdDataInfo psuInfo;
    private ThirdPartyInfo tppInfo;
    private Integer allowedFrequencyPerDay;
    private int requestedFrequencyPerDay;
    private AccountAccessInfo access;
    @JsonDeserialize(
        using = LocalDateDeserializer.class
    )
    @JsonSerialize(
        using = LocalDateSerializer.class
    )
    private LocalDate validUntil;
    private boolean recurringIndicator;
    private boolean tppRedirectPreferred;
    private boolean combinedServiceIndicator;
    private UserAccountAccessType availableAccounts;
    private UserAccountAccessType allPsd2;
    private UserAccountAccessType availableAccountsWithBalance;
}
