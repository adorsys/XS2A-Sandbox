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

package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "TPP Operation info management")
public interface TppOperationInfoRestApi {
    String BASE_PATH = "/tpp/operation";

    @GetMapping
    @ApiOperation(value = "Retrieves all Operation infos for current TPP", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<OperationInfo>> getAllOperations(@RequestParam(value = "operationType", required = false) OperationType operationType);

    @PostMapping
    @ApiOperation(value = "Adds new Operation info to database", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<OperationInfo> addOperationInfo(@RequestBody OperationInfo operationInfo);

    @DeleteMapping
    @ApiOperation(value = "Removes Operation info by id", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Void> deleteOperationInfo(@RequestParam(value = "operationInfoId") Long operationInfoId);
}
