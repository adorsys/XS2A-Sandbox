package de.adorsys.psd2.sandbox.admin.rest.client;

import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminBaseRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "tadminBaseRestClient", url = "${admin.app.url}", path = AdminBaseRestApi.BASE_PATH)
public interface AdminBaseRestClient extends AdminBaseRestApi {
}
