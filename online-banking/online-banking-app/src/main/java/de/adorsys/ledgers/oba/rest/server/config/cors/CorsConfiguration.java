package de.adorsys.ledgers.oba.rest.server.config.cors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CorsConfiguration implements WebMvcConfigurer {
    private final CorsConfigProperties corsConfigProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        log.info("Enable CORS with following configuration: [{}]", corsConfigProperties);
        registry.addMapping("/**")
            .allowedOrigins(getTargetParameters(corsConfigProperties.getAllowedOrigins()))
            .allowedMethods(getTargetParameters(corsConfigProperties.getAllowedMethods()))
            .allowedHeaders(getTargetParameters(corsConfigProperties.getAllowedHeaders()))
            .allowCredentials(corsConfigProperties.getAllowCredentials())
            .maxAge(corsConfigProperties.getMaxAge());
    }

    private String[] getTargetParameters(List<String> targetParameters) {
        return targetParameters.toArray(new String[0]);
    }
}
