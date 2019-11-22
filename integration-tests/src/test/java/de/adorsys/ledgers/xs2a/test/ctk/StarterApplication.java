package de.adorsys.ledgers.xs2a.test.ctk;

import de.adorsys.ledgers.oba.rest.client.ObaAisApiClient;
import de.adorsys.ledgers.xs2a.client.PaymentApiClient;
import de.adorsys.ledgers.xs2a.ctk.EnableStarter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackageClasses = {PaymentApiClient.class, ObaAisApiClient.class})
@SpringBootApplication
@EnableStarter
@ComponentScan(basePackages = {"de.adorsys.psd2.mapper"})
public class StarterApplication {
    public static void main(String[] args) {
        SpringApplication.run(StarterApplication.class, args);
    }

}
