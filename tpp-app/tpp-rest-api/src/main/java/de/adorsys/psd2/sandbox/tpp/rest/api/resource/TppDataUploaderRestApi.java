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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Tag(name = "TPP data uploader")
public interface TppDataUploaderRestApi {
    String BASE_PATH = TppRestApi.BASE_PATH + "/data";

    @Operation(summary = "Upload YAML file with basic test data")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/upload")
    ResponseEntity<String> uploadData(@RequestBody MultipartFile file);

    @Operation(summary = "Generate test data and upload it to Ledgers")
    @SecurityRequirement(name = "apiKey")
    @GetMapping(value = "/generate/{currency}")
    ResponseEntity<Resource> generateData(@RequestParam("generatePayments") boolean generatePayments, @PathVariable("currency") String currency);

    @Operation(summary = "Generate random IBAN")
    @SecurityRequirement(name = "apiKey")
    @GetMapping("/generate/iban")
    ResponseEntity<String> generateIban(@RequestParam("tppId") String tppId);

    @Operation(summary = "Upload CSV file with transactions list")
    @SecurityRequirement(name = "apiKey")
    @PutMapping("/upload/transactions")
    ResponseEntity<Map<String, String>> uploadTransactions(@RequestBody MultipartFile file);

    @GetMapping("/example")
    @Operation(summary = "Download transaction template")
    @SecurityRequirement(name = "apiKey")
    ResponseEntity<Resource> downloadTransactionTemplate();
}
