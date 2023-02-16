/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Online Banking Decoupled")
public interface ObaDecoupledAPI {
    String BASE_PATH = "/api/v1/decoupled";

    @Operation(summary = "Confirm/Cancel Decoupled operation")
    @SecurityRequirement(name = "apiKey")
    @PostMapping(path = "/execute")
    ResponseEntity<Boolean> decoupled(@RequestBody DecoupledConfRequest message);

    @Operation(summary = "Send decoupled notification to users device")
    @SecurityRequirement(name = "apiKey")
    @PostMapping(path = "/message")
    ResponseEntity<Boolean> sendNotification(@RequestBody DecoupledConfRequest message);
}
