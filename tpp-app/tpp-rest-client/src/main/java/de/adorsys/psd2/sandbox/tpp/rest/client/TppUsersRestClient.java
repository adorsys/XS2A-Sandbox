package de.adorsys.psd2.sandbox.tpp.rest.client;

import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppUsersRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tppUsersRestClient", url = "${tpp.app.url}", path = TppUsersRestApi.BASE_PATH)
public interface TppUsersRestClient extends TppUsersRestApi {
}
