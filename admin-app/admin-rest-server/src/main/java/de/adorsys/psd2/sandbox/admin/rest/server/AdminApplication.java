package de.adorsys.psd2.sandbox.admin.rest.server;

import de.adorsys.ledgers.keycloak.client.KeycloakClientConfiguration;
import de.adorsys.ledgers.middleware.client.EnableLedgersMiddlewareRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.admin.rest.server.config.AdminFeignConfig;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableLedgersMiddlewareRestClient
@Import(KeycloakClientConfiguration.class)
@EnableFeignClients(basePackageClasses = {UserMgmtStaffRestClient.class, UserMgmtRestClient.class}, defaultConfiguration = AdminFeignConfig.class)
@SpringBootApplication(scanBasePackages = {"de.adorsys.psd2.mapper", "de.adorsys.psd2.sandbox.admin.rest.server"})
public class AdminApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(AdminApplication.class).run(args);
    }
}
