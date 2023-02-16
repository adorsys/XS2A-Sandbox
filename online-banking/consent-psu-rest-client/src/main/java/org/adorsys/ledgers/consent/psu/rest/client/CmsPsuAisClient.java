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
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.consent.api.ais.CmsAisConsentResponse;
import de.adorsys.psd2.consent.psu.api.CmsPsuAuthorisation;
import de.adorsys.psd2.consent.psu.api.PsuHeadersDescription;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentAccessRequest;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisPsuDataAuthorisation;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.sca.AuthenticationDataHolder;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsPsuAis", url = "${cms.url}", path = "psu-api/v1/ais/consent", primary = false, configuration = FeignConfig.class)
public interface CmsPsuAisClient {

    @PutMapping(path = "/{consent-id}/authorisation/{authorisation-id}/psu-data")
    ResponseEntity<Object> updatePsuDataInConsent(
        @SuppressWarnings("unused")
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @PathVariable(CmsConstant.PATH.AUTHORISATION_ID) String authorisationId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestBody PsuIdData psuIdData);

    @PutMapping(path = "/{consent-id}/authorisation/{authorisation-id}/status/{status}")
    ResponseEntity<Object> updateAuthorisationStatus(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @PathVariable(CmsConstant.PATH.STATUS) String status,
        @PathVariable(CmsConstant.PATH.AUTHORISATION_ID) String authorisationId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID, required = false) String psuId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID_TYPE, required = false) String psuIdType,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID, required = false) String psuCorporateId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID_TYPE, required = false) String psuCorporateIdType,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestBody(required = false) AuthenticationDataHolder authenticationDataHolder);

    @PutMapping(path = "/{consent-id}/confirm-consent")
    ResponseEntity<Boolean> confirmConsent(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @PutMapping(path = "/{consent-id}/reject-consent")
    ResponseEntity<Boolean> rejectConsent(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/consents")
    @PsuHeadersDescription
    ResponseEntity<List<CmsAisAccountConsent>> getConsentsForPsu(
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID, required = false) String psuId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID_TYPE, required = false) String psuIdType,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID, required = false) String psuCorporateId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID_TYPE, required = false) String psuCorporateIdType,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestParam(value = CmsConstant.QUERY.ADDITIONAL_TPP_INFO, required = false) String additionalTppInfo,
        @RequestParam(value = CmsConstant.QUERY.STATUS, required = false) List<String> status,
        @RequestParam(value = CmsConstant.QUERY.ACCOUNT_NUMBER, required = false) List<String> accountNumbers,
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, required = false) Integer pageIndex,
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, required = false) Integer itemsPerPage);

    @PutMapping(path = "/{consent-id}/revoke-consent")
    ResponseEntity<Boolean> revokeConsent(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @PutMapping(path = "/{consent-id}/authorise-partially-consent")
    @PsuHeadersDescription
    ResponseEntity<Boolean> authorisePartiallyConsent(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/redirect/{redirect-id}")
    ResponseEntity<CmsAisConsentResponse> getConsentIdByRedirectId(
        @PathVariable(CmsConstant.PATH.REDIRECT_ID) String redirectId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/{consent-id}")
    @PsuHeadersDescription
    ResponseEntity<CmsAisAccountConsent> getConsentByConsentId(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID, required = false) String psuId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_ID_TYPE, required = false) String psuIdType,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID, required = false) String psuCorporateId,
        @RequestHeader(value = CmsConstant.HEADERS.PSU_CORPORATE_ID_TYPE, required = false) String psuCorporateIdType,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "authorisation/{authorisation-id}")
    ResponseEntity<CmsPsuAuthorisation> getAuthorisationByAuthorisationId(
        @PathVariable(CmsConstant.PATH.AUTHORISATION_ID) String authorisationId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @PutMapping(path = "/{consent-id}/save-access")
    ResponseEntity<Void> putAccountAccessInConsent(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestBody CmsAisConsentAccessRequest accountAccessRequest,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId);

    @GetMapping(path = "/{consent-id}/authorisation/psus")
    ResponseEntity<List<CmsAisPsuDataAuthorisation>> psuDataAuthorisations(
        @PathVariable(CmsConstant.PATH.CONSENT_ID) String consentId,
        @RequestHeader(value = CmsConstant.HEADERS.INSTANCE_ID, required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, required = false) Integer pageIndex,
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, required = false) Integer itemsPerPage);
}
