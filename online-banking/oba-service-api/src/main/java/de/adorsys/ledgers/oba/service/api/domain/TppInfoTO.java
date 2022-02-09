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

package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import lombok.Data;

import java.util.List;

@Data
public class TppInfoTO {
    private Long id;
    private String authorisationNumber;
    private String tppName;
    private List<TppRole> tppRoles;
    private String authorityId;
    private String authorityName;
    private String country;
    private String organisation;
    private String organisationUnit;
    private String city;
    private String state;
}
