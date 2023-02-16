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

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Online Banking PIS Cancellation.")
public interface ObaCancellationApi {
    String BASE_PATH = "/api/v1/payment/cancellation";

    @PostMapping
    @Operation(summary = "Initiate payment cancellation process")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<GlobalScaResponseTO> initCancellation(@RequestParam String paymentId);

    @PostMapping("/sca")
    @Operation(summary = "Select Sca Method for payment cancellation")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<GlobalScaResponseTO> selectSca(@RequestParam String paymentId, @RequestParam String cancellationId, @RequestParam String scaMethodId);

    @PutMapping("/confirmation")
    @Operation(summary = "Confirm payment cancellation with TAN")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<Void> validateTAN(@RequestParam String paymentId, @RequestParam String cancellationId, @RequestParam String authCode);
}
