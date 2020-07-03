package de.adorsys.psd2.sandbox.tpp.rest.client;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppAccountsRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tppAccountsRestClient", url = "${tpp.app.url}", path = TppAccountsRestApi.BASE_PATH)
public interface TppAccountsRestClient extends TppAccountsRestApi {
}
