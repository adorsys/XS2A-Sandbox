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
import de.adorsys.psd2.consent.api.pis.CmsBasePaymentResponse;
import de.adorsys.psd2.consent.psu.api.CmsPsuAuthorisation;
import de.adorsys.psd2.consent.psu.api.pis.CmsPisPsuDataAuthorisation;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsPsuPis", url = "${cms.url}", path = "psu-api/v1/payment", primary = false, configuration = FeignConfig.class)
public interface CmsPsuPisClient {

    @PutMapping(path = "/authorisation/{authorisation-id}/psu-data")
    ResponseEntity<Object> updatePsuInPayment(
        @PathVariable("authorisation-id") String authorisationId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestBody PsuIdData psuIdData);

    @PutMapping(path = "/{payment-service}/{payment-product}/{payment-id}")
    ResponseEntity<Object> updatePayment(@PathVariable("payment-id") String paymentId,

                                         @PathVariable("payment-service") String paymentService,
                                         @PathVariable("payment-product") String paymentProduct,

                                         @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
                                         @RequestBody Object body);

    @GetMapping(path = "/redirect/{redirect-id}")
    ResponseEntity<Object> getPaymentIdByRedirectId(
        @PathVariable("redirect-id") String redirectId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/{payment-id}")
    ResponseEntity<CmsBasePaymentResponse> getPaymentByPaymentId(
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @PathVariable("payment-id") String paymentId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/cancellation/redirect/{redirect-id}")
    ResponseEntity<Object> getPaymentIdByRedirectIdForCancellation(
        @PathVariable("redirect-id") String redirectId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/cancellation/{payment-id}")
    ResponseEntity<CmsBasePaymentResponse> getPaymentByPaymentIdForCancellation(
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @PathVariable("payment-id") String paymentId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "authorisation/{authorisation-id}")
    ResponseEntity<CmsPsuAuthorisation> getAuthorisationByAuthorisationId(
        @PathVariable("authorisation-id") String authorisationId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @PutMapping(path = "/{payment-id}/authorisation/{authorisation-id}/status/{status}")
    ResponseEntity<Object> updateAuthorisationStatus(
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @PathVariable("payment-id") String paymentId,
        @PathVariable("authorisation-id") String authorisationId,
        @PathVariable("status") String status,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestBody(required = false) AuthenticationDataHolder authenticationDataHolder);

    @PutMapping(path = "/{payment-id}/status/{status}")
    ResponseEntity<Void> updatePaymentStatus(
        @PathVariable("payment-id") String paymentId,
        @PathVariable("status") String status,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/{payment-id}/authorisation/psus")
    ResponseEntity<List<CmsPisPsuDataAuthorisation>> psuAuthorisationStatuses(
        @PathVariable("payment-id") String paymentId,
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, required = false) Integer pageIndex,
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, required = false) Integer itemsPerPage);
}
