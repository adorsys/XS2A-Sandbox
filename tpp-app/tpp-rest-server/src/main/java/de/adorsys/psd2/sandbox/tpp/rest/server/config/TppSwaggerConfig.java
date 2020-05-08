package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static de.adorsys.psd2.sandbox.tpp.rest.server.auth.SecurityConstant.AUTHORIZATION_HEADER;
import static java.util.Collections.singletonList;
import static springfox.documentation.swagger.web.SecurityConfigurationBuilder.builder;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class TppSwaggerConfig {
    private final BuildProperties buildProperties;

    @Bean
    public Docket apiDocklet() {
        return new Docket(DocumentationType.SWAGGER_2)
                   .apiInfo(new ApiInfoBuilder()
                                .title("TPP backend application")
                                .description("TPP backend application of PSD2 Sandbox Environment")
                                .contact(new Contact(
                                    "Adorsys GmbH & Co. KG",
                                    "https://adorsys.de",
                                    "fpo@adorsys.de")
                                )
                                .version(buildProperties.getVersion() + " " + buildProperties.get("build.number"))
                                .build())
                   .groupName("TPP-API")
                   .select()
                   .apis(RequestHandlerSelectors
                             .basePackage("de.adorsys.psd2.sandbox.tpp.rest"))
                   .paths(PathSelectors.any())
                   .build()
                   .securitySchemes(singletonList(securitySchema()));
    }

    private ApiKey securitySchema() {
        return new ApiKey("apiKey", AUTHORIZATION_HEADER, "header");
    }

    @Bean
    public SecurityConfiguration security() {
        return builder()
                   .clientId(null)
                   .clientSecret(null)
                   .realm(null)
                   .appName(null)
                   .scopeSeparator(",")
                   .useBasicAuthenticationWithAccessCodeGrant(false)
                   .build();
    }
}
