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

import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "TPP PIIS consent management")
public interface TppPiisConsentRestApi {
    String BASE_PATH = "/tpp/piis-consents";

    @ApiOperation(value = "Create PIIS consent",
        notes = "Endpoint to create PIIS consent for the given PSU",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping
    ResponseEntity<SCAConsentResponseTO> createPiisConsent(@RequestParam("userLogin") String userLogin,
                                                           @RequestParam("password") String password,
                                                           @RequestBody PiisConsent piisConsent);

    @ApiOperation(value = "Get PIIS consents",
        notes = "Endpoint to get the list of PIIS consents for the given PSU with pagination",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping
    ResponseEntity<CustomPageImpl<PiisConsent>> getPiisConsents(
        @RequestParam(value = "userLogin") String userLogin,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "25") int size);

    @ApiOperation(value = "Get PIIS consent details",
        notes = "Endpoint to get the details of PIIS consent by its ID",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/{consentId}")
    ResponseEntity<PiisConsent> getPiisConsent(
        @PathVariable(value = "consentId") String consentId);

}
