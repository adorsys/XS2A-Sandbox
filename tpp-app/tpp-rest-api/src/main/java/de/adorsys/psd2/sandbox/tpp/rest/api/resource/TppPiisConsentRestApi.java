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

import de.adorsys.ledgers.middleware.api.domain.sca.SCAConsentResponseTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TPP PIIS consent management")
public interface TppPiisConsentRestApi {
    String BASE_PATH = "/tpp/piis-consents";

    @Operation(summary = "Create PIIS consent", description = "Endpoint to create PIIS consent for the given PSU")
    @SecurityRequirement(name = "apiKey")
    @PostMapping
    ResponseEntity<SCAConsentResponseTO> createPiisConsent(@RequestParam("userLogin") String userLogin,
                                                           @RequestParam("password") String password,
                                                           @RequestBody PiisConsent piisConsent);

    @Operation(summary = "Get PIIS consents", description = "Endpoint to get the list of PIIS consents for the given PSU with pagination")
    @SecurityRequirement(name = "apiKey")
    @GetMapping
    ResponseEntity<CustomPageImpl<PiisConsent>> getPiisConsents(
        @RequestParam(value = "userLogin") String userLogin,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "25") int size);

    @Operation(summary = "Get PIIS consent details", description = "Endpoint to get the details of PIIS consent by its ID")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/{consentId}")
    ResponseEntity<PiisConsent> getPiisConsent(
        @RequestParam("userLogin") String userLogin,
        @PathVariable(value = "consentId") String consentId);

    @Operation(summary = "Terminate the given PIIS consent", description = "Changes the definite PIIS consent status to TERMINATED_BY_ASPSP")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/{consentId}/terminate")
    ResponseEntity<Void> terminatePiisConsent(@PathVariable(value = "consentId") String consentId);

}
