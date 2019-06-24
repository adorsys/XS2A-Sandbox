package de.adorsys.psd2.sandbox.tpp.rest.client;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tppRestClient", url = "${tpp-app.url}", path = TppRestApi.BASE_PATH)
public interface TppRestClient extends TppRestApi {
}
