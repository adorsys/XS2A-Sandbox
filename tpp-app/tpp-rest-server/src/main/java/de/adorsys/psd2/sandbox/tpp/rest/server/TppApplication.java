package de.adorsys.psd2.sandbox.tpp.rest.server;

import de.adorsys.ledgers.keycloak.client.KeycloakClientConfiguration;
import de.adorsys.ledgers.middleware.client.EnableLedgersMiddlewareRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.TppUiBeFeignConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableLedgersMiddlewareRestClient
@Import(KeycloakClientConfiguration.class)
@EnableFeignClients(basePackageClasses = {UserMgmtStaffRestClient.class, UserMgmtRestClient.class}, defaultConfiguration = TppUiBeFeignConfiguration.class)
@SpringBootApplication(scanBasePackages = {"de.adorsys.psd2.mapper", "de.adorsys.psd2.sandbox.tpp.rest.server"})
public class TppApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TppApplication.class).run(args);
    }
}
