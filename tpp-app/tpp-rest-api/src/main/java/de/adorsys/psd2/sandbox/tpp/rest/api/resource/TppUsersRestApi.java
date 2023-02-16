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

package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "TPP Users management")
public interface TppUsersRestApi {
    String BASE_PATH = "/tpp/users";
    String USER_ID = "userId";

    @Operation(summary = "Create users for a given TPP", description = "Endpoint to create a user for a given TPP")
    @SecurityRequirement(name = "apiKey")
    @PostMapping
    ResponseEntity<UserTO> createUser(@RequestBody User user);

    @Operation(summary = "List users for a given TPP", description = "Endpoint to lists users for a given TPP")
    @SecurityRequirement(name = "apiKey")
    @GetMapping
    ResponseEntity<CustomPageImpl<UserTO>> getAllUsers(
        @RequestParam(value = "queryParam", required = false, defaultValue = "") String queryParam,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "25") int size);

    @Operation(summary = "Update user for a given TPP", description = "Endpoint to update a user for a given TPP")
    @SecurityRequirement(name = "apiKey")
    @PutMapping
    ResponseEntity<Void> updateUser(@RequestBody User user);

    @Operation(summary = "Retrieves user by id", description = "Endpoint to get user by id")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/{userId}")
    ResponseEntity<UserTO> getUser(@PathVariable("userId") String userId);

    @Operation(summary = "Get current user")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/me")
    ResponseEntity<UserTO> getSelf();

    @Operation(summary = "Block/Unblock User", description = "Changes block state for given user, returns status being set to the block")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/status")
    ResponseEntity<Boolean> changeStatus(@RequestParam(value = USER_ID) String userId);

    @Operation(summary = "Send link to update password")
    @PostMapping(value = "/reset/password/{login}")
    ResponseEntity<Void> resetPasswordViaEmail(@PathVariable("login") String login);
}
