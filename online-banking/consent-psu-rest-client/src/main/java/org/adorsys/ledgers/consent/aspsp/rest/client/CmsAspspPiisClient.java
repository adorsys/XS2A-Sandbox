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

package org.adorsys.ledgers.consent.aspsp.rest.client;

import java.util.List;

import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import de.adorsys.psd2.xs2a.core.piis.PiisConsent;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@FeignClient(value = "cmsAspspPiisClient", url = "${cms.url}", path="/aspsp-api/v1/piis/consents", primary=false, configuration=FeignConfig.class)
@Api(value = "aspsp-api/v1/piis/consents", tags = "ASPSP PIIS, Consents", description = "Controller for cms-aspsp-api providing access to PIIS consents")
public interface CmsAspspPiisClient {

    String DEFAULT_SERVICE_INSTANCE_ID = "UNDEFINED";

    @PostMapping(path = "/")
    @ApiOperation(value = "Creates new PIIS consent")
    @ApiResponses(value = {
        @ApiResponse(code = 201, message = "Created", response = String.class),
        @ApiResponse(code = 400, message = "Bad Request")})
    ResponseEntity<CreatePiisConsentResponse> createConsent(@RequestBody CreatePiisConsentRequest request,
                                                                   @ApiParam(value = "Client ID of the PSU in the ASPSP client interface. Might be mandated in the ASPSP's documentation. Is not contained if an OAuth2 based authentication was performed in a pre-step or an OAuth2 based SCA was performed in an preceeding AIS service in the same session. ")
                                                                   @RequestHeader(value = "psu-id", required = false) String psuId,
                                                                   @ApiParam(value = "Type of the PSU-ID, needed in scenarios where PSUs have several PSU-IDs as access possibility. ")
                                                                   @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
                                                                   @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
                                                                   @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
                                                                   @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
                                                                   @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType);

    @GetMapping(path = "/")
    @ApiOperation(value = "Returns a list of PIIS Consent objects by PSU ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK"),
        @ApiResponse(code = 404, message = "Not Found")})
    ResponseEntity<List<PiisConsent>> getConsentsForPsu(
        @ApiParam(value = "Client ID of the PSU in the ASPSP client interface. Might be mandated in the ASPSP's documentation. Is not contained if an OAuth2 based authentication was performed in a pre-step or an OAuth2 based SCA was performed in an preceeding AIS service in the same session. ")
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @ApiParam(value = "Type of the PSU-ID, needed in scenarios where PSUs have several PSU-IDs as access possibility. ")
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @ApiParam(value = "ID of the particular service instance")
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @DeleteMapping(path = "/{consent-id}")
    @ApiOperation(value = "Terminates PIIS Consent object by its ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK", response = Boolean.class),
        @ApiResponse(code = 404, message = "Not Found")})
    ResponseEntity<Boolean> terminateConsent(
        @ApiParam(name = "consent-id", value = "The account consent identification assigned to the created account consent.", example = "bf489af6-a2cb-4b75-b71d-d66d58b934d7")
        @PathVariable("consent-id") String consentId,
        @ApiParam(value = "ID of the particular service instance")
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);
}
