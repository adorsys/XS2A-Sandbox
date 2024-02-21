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

package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TPP Revert Point API")
public interface TppRecoveryPointRestApi {
    String BASE_PATH = "/tpp/recovery";

    @Operation(summary = "Get recovery point by id")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/point/{id}")
    ResponseEntity<RecoveryPointTO> point(@PathVariable("id") Long id);

    @Operation(summary = "Get all recovery points for TPP")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/points")
    ResponseEntity<List<RecoveryPointTO>> points();

    @Operation(summary = "Create a new recovery point")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/point")
    ResponseEntity<Void> createPoint(@RequestBody RecoveryPointTO recoveryPoint);

    @Operation(summary = "Remove existing recovery point by id")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/point/{id}")
    ResponseEntity<Void> deletePoint(@PathVariable("id") Long id);
}
