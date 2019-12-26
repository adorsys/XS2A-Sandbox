package de.adorsys.ledgers.oba.rest.server.config.cors;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "server.servlet.session.cookie")
public class CookieConfigProperties {
    private boolean secure = false;
    private int maxAge = 300;
    private boolean httpOnly = true;
    private String path = "/";
}
