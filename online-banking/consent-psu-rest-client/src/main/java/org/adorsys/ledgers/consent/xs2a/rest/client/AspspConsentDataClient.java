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

package org.adorsys.ledgers.consent.xs2a.rest.client;

import de.adorsys.psd2.consent.api.CmsAspspConsentDataBase64;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "aspspConsentDataClient", url = "${cms.url}", path = "psu-api/v1/aspsp-consent-data/consents", primary = false, configuration = FeignConfig.class)
public interface AspspConsentDataClient {

    @GetMapping("/{consent-id}")
    ResponseEntity<CmsAspspConsentDataBase64> getAspspConsentData(
        @PathVariable("consent-id") String encryptedConsentId);

    @PutMapping("/{consent-id}")
    ResponseEntity<Void> updateAspspConsentData(
        @PathVariable("consent-id") String encryptedConsentId,
        @RequestBody CmsAspspConsentDataBase64 request);

    @DeleteMapping("/{consent-id}")
    ResponseEntity<Void> deleteAspspConsentData(
        @PathVariable("consent-id") String encryptedConsentId);
}
