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

package de.adorsys.psd2.sandbox.admin.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Admin Users management")
public interface AdminUsersRestApi {
    String BASE_PATH = "/admin/users";
    String USER_ID = "userId";

    @ApiOperation(value = "Create users for a given TPP",
        notes = "Endpoint to create a user for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping
    ResponseEntity<UserTO> createUser(@RequestBody User user);

    @ApiOperation(value = "List users for a given TPP",
        notes = "Endpoint to lists users for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping
    ResponseEntity<CustomPageImpl<UserTO>> getAllUsers(
        @RequestParam(value = "queryParam", required = false, defaultValue = "") String queryParam,
        @RequestParam(value = "page", required = false, defaultValue = "0") int page,
        @RequestParam(value = "size", required = false, defaultValue = "25") int size);

    @ApiOperation(value = "Update user for a given TPP",
        notes = "Endpoint to update a user for a given TPP",
        authorizations = @Authorization(value = "apiKey"))
    @PutMapping
    ResponseEntity<Void> updateUser(@RequestBody User user);

    @ApiOperation(value = "Retrieves user by id",
        notes = "Endpoint to get user by id",
        authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/{userId}")
    ResponseEntity<UserTO> getUser(@PathVariable("userId") String userId);

    @ApiOperation(value = "Get current user", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/me")
    ResponseEntity<UserTO> getSelf();

    @ApiOperation(value = "Block/Unblock User",
        notes = "Changes block state for given user, returns status being set to the block",
        authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/status")
    ResponseEntity<Boolean> changeStatus(@RequestParam(value = USER_ID) String userId);

    @ApiOperation(value = "Send link to update password")
    @PostMapping(value = "/reset/password/{login}")
    ResponseEntity<Void> resetPasswordViaEmail(@PathVariable("login") String login);
}
