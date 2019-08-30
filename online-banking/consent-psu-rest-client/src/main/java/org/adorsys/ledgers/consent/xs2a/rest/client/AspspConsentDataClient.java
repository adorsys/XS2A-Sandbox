/*
 * Copyright 2018-2018 adorsys GmbH & Co KG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.adorsys.ledgers.consent.xs2a.rest.client;

import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@FeignClient(value = "aspspConsentDataClient", url = "${cms.url}", path="/api/v1/aspsp-consent-data/consents", primary=false, configuration=FeignConfig.class)
@Api(value = "api/v1/aspsp-consent-data", tags = "Aspsp Consent Data", description = "Provides access to consent management system for AspspDataConsent")
public interface AspspConsentDataClient {

	@GetMapping("/{consent-id}")
    @ApiOperation(value = "Get aspsp consent data identified by given consent id / payment id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    ResponseEntity<CmsAspspConsentDataBase64> getAspspConsentData(
        @ApiParam(
            name = "consent-id",
            value = "The account consent identification assigned to the created account consent / payment identification assigned to the created payment.",
            example = "6cb3773c-2888-40cc-86fa-10589de6d77f")
        @PathVariable("consent-id") String consentId);

    @PutMapping("/{consent-id}")
    @ApiOperation(value = "Update aspsp consent data identified by given consent id / payment id.")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    ResponseEntity updateAspspConsentData(
        @ApiParam(
            name = "consent-id",
            value = "The account consent identification assigned to the created account consent / payment identification assigned to the created payment.",
            example = "CxymMkwtykFtTeQuH1jrcoOyzcqCcwNCt5193Gfn33mqqcAy_xw2KPwMd5y6Xxe1EwE0BTNRHeyM0FI90wh0hA==_=_bS6p6XvTWI")
        @PathVariable("consent-id") String encryptedConsentId,
        @RequestBody CmsAspspConsentDataBase64 request);

    @DeleteMapping("/{consent-id}")
    @ApiOperation(value = "Delete aspsp consent data identified by given consent id / payment id.")
    @ApiResponses(value = {
        @ApiResponse(code = 204, message = "No Content"),
        @ApiResponse(code = 404, message = "Not Found")})
    ResponseEntity deleteAspspConsentData(
        @ApiParam(
            name = "consent-id",
            value = "The account consent identification assigned to the created account consent / payment identification assigned to the created payment.",
            example = "CxymMkwtykFtTeQuH1jrcoOyzcqCcwNCt5193Gfn33mqqcAy_xw2KPwMd5y6Xxe1EwE0BTNRHeyM0FI90wh0hA==_=_bS6p6XvTWI")
        @PathVariable("consent-id") String encryptedConsentId);
}
