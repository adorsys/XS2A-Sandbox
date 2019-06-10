package de.adorsys.psd2.sandbox.tpp.rest.client;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppDataUploaderApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tppUploaderApiClient", url = "${tpp-app.url}", configuration = FeignConfig.class)
public interface TppDataUploaderApiClient extends TppDataUploaderApi {
}
