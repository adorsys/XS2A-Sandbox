package org.adorsys.ledgers.consent.aspsp.rest.client;

import de.adorsys.psd2.consent.api.CmsConstant;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.adorsys.ledgers.consent.mixin.ResponseDataMixIn;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collection;

import static de.adorsys.psd2.consent.aspsp.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsAspspAisClient", url = "${cms.url}", primary = false, configuration = FeignConfig.class)
@RequestMapping(path = "aspsp-api/v1/ais/consents")
public interface CmsAspspAisClient /*extends CmsAspspAisExportApi*/ { //TODO restore extention of API after fix on Xs2a side

    @GetMapping(path = "/psu")
    @ApiOperation(value = "Returns a list of AIS consent objects by given mandatory PSU ID Data, optional creation date and instance ID")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "OK")})
    ResponseDataMixIn<Collection<CmsAisAccountConsent>> getConsentsByPsu(
        @ApiParam(value = "Creation start date", example = "2010-01-01")
        @RequestHeader(value = "start-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @ApiParam(value = "Creation end date", example = "2030-01-01")
        @RequestHeader(value = "end-date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
        @ApiParam(value = "Client ID of the PSU in the ASPSP client interface. Might be mandated in the ASPSP's documentation. Is not contained if an OAuth2 based authentication was performed in a pre-step or an OAuth2 based SCA was performed in an preceding AIS service in the same session. ")
        @RequestHeader(value = "psu-id", required = false) String psuId,
        @ApiParam(value = "Type of the PSU-ID, needed in scenarios where PSUs have several PSU-IDs as access possibility. ")
        @RequestHeader(value = "psu-id-type", required = false) String psuIdType,
        @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
        @RequestHeader(value = "psu-corporate-id", required = false) String psuCorporateId,
        @ApiParam(value = "Might be mandated in the ASPSP's documentation. Only used in a corporate context. ")
        @RequestHeader(value = "psu-corporate-id-type", required = false) String psuCorporateIdType,
        @ApiParam(value = "ID of the particular service instance")
        @RequestHeader(value = "instance-id", required = false, defaultValue = DEFAULT_SERVICE_INSTANCE_ID) String instanceId,
        @ApiParam(value = "Index of current page", example = "0")
        @RequestParam(value = CmsConstant.QUERY.PAGE_INDEX, defaultValue = "0") Integer pageIndex,
        @ApiParam(value = "Quantity of consents on one page", example = "20")
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, defaultValue = "20") Integer itemsPerPage,
        @RequestParam(value = CmsConstant.QUERY.ADDITIONAL_TPP_INFO, required = false) String additionalTppInfo);
}
