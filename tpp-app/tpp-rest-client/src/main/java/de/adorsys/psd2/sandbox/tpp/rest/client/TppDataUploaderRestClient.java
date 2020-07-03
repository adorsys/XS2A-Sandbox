package de.adorsys.psd2.sandbox.tpp.rest.client;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tppUploaderRestClient", url = "${tpp.app.url}", path = TppDataUploaderRestApi.BASE_PATH)
public interface TppDataUploaderRestClient extends TppDataUploaderRestApi {
}
