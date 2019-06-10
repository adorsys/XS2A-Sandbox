package de.adorsys.psd2.sandbox.tpp.rest.server;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class TppApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TppApplication.class).run(args);
    }
}
