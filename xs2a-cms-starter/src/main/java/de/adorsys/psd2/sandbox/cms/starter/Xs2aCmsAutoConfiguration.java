package de.adorsys.psd2.sandbox.cms.starter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Slf4j
@Configuration
@PropertySource("classpath:cms.properties")
@Import(value = Xs2aCmsConfig.class)
public class Xs2aCmsAutoConfiguration {

    public Xs2aCmsAutoConfiguration() {
        log.info("------- XS2A cms auto configuration was loaded -------");
    }
}
