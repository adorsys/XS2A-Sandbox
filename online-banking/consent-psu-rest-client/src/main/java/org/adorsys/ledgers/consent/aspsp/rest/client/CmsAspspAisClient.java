package org.adorsys.ledgers.consent.aspsp.rest.client;

import de.adorsys.psd2.consent.aspsp.api.CmsAspspAisExportApi;
import org.adorsys.ledgers.consent.xs2a.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "cmsAspspAisClient", url = "${cms.url}", primary = false, configuration = FeignConfig.class)
public interface CmsAspspAisClient extends CmsAspspAisExportApi {
}
