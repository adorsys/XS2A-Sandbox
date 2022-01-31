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

package de.adorsys.ledgers.oba.rest.server.config.swagger;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;

import static springfox.documentation.swagger.web.SecurityConfigurationBuilder.builder;

@Configuration
@EnableSwagger2
@RequiredArgsConstructor
public class SwaggerConfig {
    private final BuildProperties buildProperties;

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                   .groupName("PSU-API")
                   .select()
                   .apis(RequestHandlerSelectors.basePackage("de.adorsys.ledgers.oba.rest"))
                   .paths(PathSelectors.any())
                   .build()
                   .pathMapping("/")
                   .apiInfo(metaData())
                   .securitySchemes(Collections.singletonList(apiKey()));
    }

    private ApiKey apiKey() {
        return new ApiKey("apiKey", "Authorization", "header");
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

    private ApiInfo metaData() {

        Contact contact = new Contact("Adorsys GmbH", "https://www.adorsys.de",
                                      "fpo@adorsys.de");

        return new ApiInfo(
            "Online banking",
            "Implementation of backend for online banking UI. "
                + "We have 3 preloaded users in Ledgers: <b>marion.mueller</b>, <b>anton.brueckner</b>, <b>max.musterman</b> all with the PIN <b>12345</b>. "
                + "You can use the User Login API from Ledgers <b>/users/authorise2</b> endpoint to gain an access token. Then use the access token with the prefix 'Bearer ' to Authorize on this ui.",
            buildProperties.getVersion() + " " + buildProperties.get("build.number"),
            "Terms of Service: to be edited...",
            contact,
            "Apache License Version 2.0",
            "https://www.apache.org/licenses/LICENSE-2.0",
            new ArrayList<>());
    }
}
