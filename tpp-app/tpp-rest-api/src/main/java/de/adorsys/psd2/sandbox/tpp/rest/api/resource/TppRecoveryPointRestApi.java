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

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "TPP Revert Point API")
public interface TppRecoveryPointRestApi {
    String BASE_PATH = "/tpp/recovery";

    @ApiOperation(value = "Get recovery point by id", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/point/{id}")
    ResponseEntity<RecoveryPointTO> point(@PathVariable("id") Long id);

    @ApiOperation(value = "Get all recovery points for TPP", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/points")
    ResponseEntity<List<RecoveryPointTO>> points();

    @ApiOperation(value = "Create a new recovery point", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/point")
    ResponseEntity<Void> createPoint(@RequestBody RecoveryPointTO recoveryPoint);

    @ApiOperation(value = "Remove existing recovery point by id", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/point/{id}")
    ResponseEntity<Void> deletePoint(@PathVariable("id") Long id);
}
