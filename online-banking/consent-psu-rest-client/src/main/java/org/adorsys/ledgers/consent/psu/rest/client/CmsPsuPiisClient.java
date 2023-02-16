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

package org.adorsys.ledgers.consent.psu.rest.client;

import de.adorsys.psd2.consent.api.CmsConstant;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsPsuPiis", url = "${cms.url}", path = "psu-api/v1/piis/consents", primary = false, configuration = FeignConfig.class)
public interface CmsPsuPiisClient {

    @GetMapping(path = "/{consent-id}")
    ResponseEntity<CmsPiisConsent> getConsent(
        @PathVariable("consent-id") String consentId,
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping
    ResponseEntity<List<CmsPiisConsent>> getConsentsForPsu(
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, required = false) Integer pageIndex,
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, required = false) Integer itemsPerPage);

    @PutMapping(path = "/{consent-id}/revoke-consent")
    ResponseEntity<Boolean> revokeConsent(
        @PathVariable("consent-id") String consentId,
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);
}
