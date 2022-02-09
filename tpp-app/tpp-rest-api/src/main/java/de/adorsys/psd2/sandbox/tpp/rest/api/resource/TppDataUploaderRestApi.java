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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Api(tags = "TPP data uploader")
public interface TppDataUploaderRestApi {
    String BASE_PATH = TppRestApi.BASE_PATH + "/data";

    @ApiOperation(value = "Upload YAML file with basic test data", authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/upload")
    ResponseEntity<String> uploadData(@RequestBody MultipartFile file);

    @ApiOperation(value = "Generate test data and upload it to Ledgers", authorizations = @Authorization(value = "apiKey"))
    @GetMapping(value = "/generate/{currency}")
    ResponseEntity<Resource> generateData(@RequestParam("generatePayments") boolean generatePayments, @PathVariable("currency") String currency);

    @ApiOperation(value = "Generate random IBAN", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/generate/iban")
    ResponseEntity<String> generateIban(@RequestParam("tppId") String tppId);

    @ApiOperation(value = "Upload CSV file with transactions list", authorizations = @Authorization(value = "apiKey"))
    @PutMapping("/upload/transactions")
    ResponseEntity<Map<String, String>> uploadTransactions(@RequestBody MultipartFile file);

    @GetMapping("/example")
    @ApiOperation(value = "Download transaction template", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Resource> downloadTransactionTemplate();
}
