package de.adorsys.psd2.sandbox.tpp.rest.server;

import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.cms.impl.config.EnableXs2aCms;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.TppUiBeFeignConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableXs2aCms
@EnableFeignClients(basePackageClasses = {UserMgmtStaffRestClient.class, UserMgmtRestClient.class}, defaultConfiguration = TppUiBeFeignConfiguration.class)
@SpringBootApplication
public class TppApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TppApplication.class).run(args);
    }
}
