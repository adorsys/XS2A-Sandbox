package org.adorsys.ledgers.consent.aspsp.rest.client;

import de.adorsys.psd2.consent.api.CmsConstant;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import io.swagger.annotations.Api;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collection;

import static org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient.DEFAULT_SERVICE_INSTANCE_ID;

@FeignClient(value = "cmsAspspAisClient", url = "${cms.url}", path = "aspsp-api/v1/ais/consents", primary = false, configuration = FeignConfig.class)
@Api(value = "aspsp-api/v1/ais/consents", tags = "Export AIS Consents")
public interface CmsAspspAisClient {

    @GetMapping(path = "/psu")
    ResponseData<Collection<CmsAisAccountConsent>> getConsentsByPsu(
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
        @RequestParam(value = CmsConstant.QUERY.ITEMS_PER_PAGE, defaultValue = "20") Integer itemsPerPage);
}
