/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.sandbox.admin.rest.server.config;

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

import static de.adorsys.psd2.sandbox.auth.SecurityConstant.AUTHORIZATION_HEADER;
import static java.util.Collections.singletonList;
import static springfox.documentation.swagger.web.SecurityConfigurationBuilder.builder;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class AdminSwaggerConfig {
    private final BuildProperties buildProperties;

    @Bean
    public Docket apiDocklet() {
        return new Docket(DocumentationType.SWAGGER_2)
                   .apiInfo(new ApiInfoBuilder()
                                .title("Admin backend application")
                                .description("Admin backend application of PSD2 Sandbox Environment")
                                .contact(new Contact(
                                    "Adorsys GmbH & Co. KG",
                                    "https://adorsys.de",
                                    "fpo@adorsys.de")
                                )
                                .version(buildProperties.getVersion() + " " + buildProperties.get("build.number"))
                                .build())
                   .groupName("Admin-API")
                   .select()
                   .apis(RequestHandlerSelectors
                             .basePackage("de.adorsys.psd2.sandbox.admin.rest"))
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
