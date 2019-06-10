package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class TppSwaggerConfig {

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
                                .version("1.0.0")
                                .build())
                   .groupName("TPP-API")
                   .select()
                   .apis(RequestHandlerSelectors
                             .basePackage("de.adorsys.psd2.sandbox.tpp.rest"))
                   .paths(PathSelectors.any())
                   .build();
    }
}
