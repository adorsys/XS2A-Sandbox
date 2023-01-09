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

package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Online Banking Consents")
public interface ObaConsentApi {
    String BASE_PATH = "/api/v1/consents";

    /**
     * @param userLogin login of current user
     * @return List of valid AIS Consents for user
     */
    @GetMapping(path = "/{userLogin}")
    @Operation(summary = "Get List of valid AIS Consents")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<List<ObaAisConsent>> consents(@PathVariable("userLogin") String userLogin);

    /**
     * @param userLogin login of current user
     * @return List of valid AIS Consents for user
     */
    @GetMapping(path = "/{userLogin}/paged")
    @Operation(summary = "Get List of valid AIS Consents")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<CustomPageImpl<ObaAisConsent>> consentsPaged(@PathVariable("userLogin") String userLogin,
                                                           @RequestParam(required = false, defaultValue = "0") int page,
                                                           @RequestParam(required = false, defaultValue = "25") int size);

    /**
     * @param consentId identifier of consent
     */
    @PutMapping(path = "/{consentId}")
    @Operation(summary = "Revoke consent by ID")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<Boolean> revokeConsent(@PathVariable String consentId);

    /**
     * @param consentId       decrypted consent id
     * @param authorizationId authorization id
     * @param tan             TAN for single operation
     * @return 200 OK if operation was successful, or an error with msg on the failure reason
     */
    @GetMapping(path = "/confirm/{userLogin}/{consentId}/{authorizationId}/{tan}")
    @Operation(summary = "Confirm AIS Consent for Decoupled Approach")
    ResponseEntity<Void> confirm(@PathVariable("userLogin") String userLogin,
                                 @PathVariable("consentId") String consentId,
                                 @PathVariable("authorizationId") String authorizationId,
                                 @PathVariable("tan") String tan);

    @PostMapping(path = "/piis")
    @Operation(summary = "Create PIIS consent")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<Void> createPiis(@RequestBody CreatePiisConsentRequestTO request);

    @GetMapping(path = "/tpp")
    @Operation(summary = "Retrieves list of TPPs registered at the ASPSPs CMS")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<List<TppInfoTO>> tpps();
}
