package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "iban.generator")
public class IbanGenerationConfigProperties {
    private String countryCode = "DE";
    private BankCode bankCode = new BankCode();


    @Data
    public class BankCode {
        private String nisp = "76070024";
        private String random = "76050101";
    }
}
