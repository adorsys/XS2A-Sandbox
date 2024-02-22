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

package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Online Banking Authorization. Provides access to online banking")
public interface ObaAuthorizationApi {
    String BASE_PATH = "/api/v1";

    /**
     * @param login users login
     * @param pin   users pin
     */
    @PostMapping("/login")
    @Operation(summary = "Perform Online Banking Login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @Operation(summary = "Get current user")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/me")
    ResponseEntity<UserTO> getSelf();

    @Operation(summary = "Send link to update password")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/users/reset/password/{login}")
    ResponseEntity<Void> resetPasswordViaEmail(@PathVariable("login") String login);

    @Operation(summary = "Edit current user")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/me")
    ResponseEntity<Void> editSelf(@RequestBody UserTO user);

}
