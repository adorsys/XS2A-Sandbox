package de.adorsys.psd2.sandbox.admin.rest.client;

import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminUsersRestApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "adminUsersRestClient", url = "${admin.app.url}", path = AdminUsersRestApi.BASE_PATH)
public interface AdminUsersRestClient extends AdminUsersRestApi {
}
