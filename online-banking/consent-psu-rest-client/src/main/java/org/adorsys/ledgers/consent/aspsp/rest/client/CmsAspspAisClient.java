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

package org.adorsys.ledgers.consent.aspsp.rest.client;

import de.adorsys.psd2.consent.api.CmsConstant;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import org.adorsys.ledgers.consent.mixin.ResponseDataMixIn;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collection;

import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsAspspAisClient", url = "${cms.url}", path = "aspsp-api/v1/ais/consents", primary = false, configuration = FeignConfig.class)
public interface CmsAspspAisClient {

    @GetMapping(path = "/psu")
    ResponseDataMixIn<Collection<CmsAisAccountConsent>> getConsentsByPsu(
        @RequestHeader(value = "start-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestHeader(value = "end-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, defaultValue = "0") Integer pageIndex,
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, defaultValue = "20") Integer itemsPerPage,
        @RequestParam(value = CmsConstant.QUERY.ADDITIONAL_TPP_INFO, required = false) String additionalTppInfo);
}
