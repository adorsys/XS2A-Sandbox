package de.adorsys.psd2.sandbox.admin.rest.client;

import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminAccountsRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "adminAccountsRestClient", url = "${admin.app.url}", path = AdminAccountsRestApi.BASE_PATH)
public interface AdminAccountsRestClient extends AdminAccountsRestApi {
}
