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

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "TPP Operation info management")
public interface TppOperationInfoRestApi {
    String BASE_PATH = "/tpp/operation";

    @GetMapping
    @Operation(summary = "Retrieves all Operation infos for current TPP")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<List<OperationInfo>> getAllOperations(@RequestParam(value = "operationType", required = false) OperationType operationType);

    @PostMapping
    @Operation(summary = "Adds new Operation info to database")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<OperationInfo> addOperationInfo(@RequestBody OperationInfo operationInfo);

    @DeleteMapping
    @Operation(summary = "Removes Operation info by id")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<Void> deleteOperationInfo(@RequestParam(value = "operationInfoId") Long operationInfoId);
}
