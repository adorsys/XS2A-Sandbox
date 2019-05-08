package de.adorsys.psd2.sandbox.certificate.config;

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
public class SwaggerConfig {

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                   .apiInfo(new ApiInfoBuilder()
                                .title("Certificate Generator")
                                .description("Certificate Generator for Testing Purposes of PSD2 Dynamic Sandbox")
                                .contact(new Contact(
                                    "Adorsys GmbH & Co. KG",
                                    "https://adorsys.de",
                                    "fpo@adorsys.de")
                                )
                                .version("1.0.0")
                                .build())
                   .groupName("Certificate Generator API")
                   .select()
                   .apis(RequestHandlerSelectors
                             .basePackage("de.adorsys.psd2.sandbox.certificate"))
                   .paths(PathSelectors.any())
                   .build();
    }
}
