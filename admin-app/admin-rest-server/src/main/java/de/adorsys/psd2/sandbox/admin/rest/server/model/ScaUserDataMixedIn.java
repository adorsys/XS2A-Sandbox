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

package de.adorsys.psd2.sandbox.admin.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;

@SuppressWarnings({"PMD.UnusedPrivateField", "java:S1068"})
@JsonIgnoreProperties(allowGetters = true)
public abstract class ScaUserDataMixedIn {
    private String id;
    private ScaMethodTypeTO scaMethod;
    private String methodValue;
    private UserTO userTO;
    private boolean usesStaticTan;
    private String staticTan;
    private boolean decoupled;
}
