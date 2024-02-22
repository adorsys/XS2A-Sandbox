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

import de.adorsys.psd2.sandbox.admin.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.admin.rest.api.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.iban4j.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.Map;
import java.util.Set;

@Tag(name = "Admin main API")
public interface AdminBaseRestApi {
    String BASE_PATH = "/admin";

    @Operation(summary = "Login for Admin")
    @PostMapping(value = "/login")
    void login(@RequestHeader(value = "login") String login, @RequestHeader(value = "pin") String pin);

    @Operation(summary = "Get supported currencies list")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/currencies")
    ResponseEntity<Set<Currency>> getCurrencies();

    @Operation(summary = "Get country codes")
    @GetMapping("/codes")
    ResponseEntity<Map<CountryCode, String>> getSupportedCountryCodes();

    @Operation(summary = "Get country code character type and country code length")
    @GetMapping("/country/codes/structure")
    ResponseEntity<BankCodeStructure> getBankCodeStructure(@RequestParam(value = "countryCode") String countryCode);

    @Operation(summary = "Generate random TPP-ID")
    @PostMapping("/id")
    ResponseEntity<String> getRandomTppId(@RequestParam(value = "countryCode") String countryCode);

    @Operation(summary = "Register new TPP")
    @PostMapping("/register")
    ResponseEntity<Void> register(@RequestBody User user);

    @Operation(summary = "Remove Tpp")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/self")
    ResponseEntity<Void> remove();

    @Operation(summary = "Remove transactions for account in Tpp")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/transactions/{accountId}")
    ResponseEntity<Void> transactions(@PathVariable(value = "accountId") String accountId);

    @Operation(summary = "Remove account")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/account/{accountId}")
    ResponseEntity<Void> account(@PathVariable(value = "accountId") String accountId);

    @Operation(summary = "Remove user")
    @SecurityRequirement(name = "apiKey")
    @DeleteMapping("/user/{userId}")
    ResponseEntity<Void> user(@PathVariable(value = "userId") String userId);

    @Operation(summary = "Consume TAN notification from CoreBanking")
    @PutMapping("/push/tan")
    ResponseEntity<Void> consumeTan(@RequestBody String tan);
}
