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

package de.adorsys.psd2.sandbox.admin.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin API")
public interface AdminRestApi {
    String BASE_PATH = "/admin/admin";

    String COUNTRY = "country";
    String TPP_ID = "tppId";
    String USER_ID = "userId";
    String TPP_LOGIN = "tppLogin";
    String USER_LOGIN = "userLogin";
    String ROLE = "role";
    String BLOCKED = "blocked";
    String IBAN_PARAM = "ibanParam";

    @Operation(summary = "Get users", description = "Retrieves Page of Users with filters")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/users")
    ResponseEntity<CustomPageImpl<UserExtendedTO>> users(@RequestParam(value = COUNTRY, defaultValue = "", required = false) String countryCode,
                                                         @RequestParam(value = TPP_ID, defaultValue = "", required = false) String tppId,
                                                         @RequestParam(value = TPP_LOGIN, defaultValue = "", required = false) String tppLogin,
                                                         @RequestParam(value = USER_LOGIN, defaultValue = "", required = false) String userLogin,
                                                         @RequestParam(value = ROLE, required = false) UserRoleTO role,
                                                         @RequestParam(value = BLOCKED, required = false) Boolean blocked,
                                                         @RequestParam("page") int page,
                                                         @RequestParam("size") int size);

    @Operation(summary = "Update user", description = "Update user")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/users")
    ResponseEntity<Void> user(@RequestBody UserTO user);

    @Operation(summary = "Retrieves Page of accounts with filters")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/account")
    ResponseEntity<CustomPageImpl<AccountDetailsExtendedTO>> accounts(@RequestParam(value = COUNTRY, defaultValue = "", required = false) String countryCode,
                                                                      @RequestParam(value = TPP_ID, defaultValue = "", required = false) String tppId,
                                                                      @RequestParam(value = TPP_LOGIN, defaultValue = "", required = false) String tppLogin,
                                                                      @RequestParam(value = IBAN_PARAM, required = false, defaultValue = "") String ibanParam,
                                                                      @RequestParam(value = BLOCKED, required = false) Boolean isBlocked,
                                                                      @RequestParam("page") int page,
                                                                      @RequestParam("size") int size);

    @Operation(summary = "Register new User")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user,
                                  @RequestParam(value = TPP_ID) String tppId);

    @Operation(summary = "Register new Admin")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/register/admin")
    ResponseEntity<Void> admin(@RequestBody User user);

    @Operation(summary = "Retrieves Users with Admin rights")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/admins")
    ResponseEntity<CustomPageImpl<UserTO>> admins(@RequestParam("page") int page,
                                                  @RequestParam("size") int size);

    @Operation(summary = "Remove Tpp")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping()
    ResponseEntity<Void> remove(String tppId);

    @Operation(summary = "Remove test Tpps (with digit names)")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/test/data")
    ResponseEntity<Void> removeAllTestData() throws InterruptedException;

    @Operation(summary = "Set password for Tpp", description = "Changes password for given Tpp")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/password")
    ResponseEntity<Void> updatePassword(@RequestParam(value = TPP_ID) String tppId, @RequestParam("pin") String password);

    @Operation(summary = "Block/Unblock User", description = "Changes block state for given user, returns status being set to the block")
    @SecurityRequirement(name = "apiKey")
    @PostMapping("/status")
    ResponseEntity<Boolean> changeStatus(@RequestParam(value = USER_ID) String userId);
}
