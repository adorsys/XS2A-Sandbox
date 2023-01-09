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

package de.adorsys.psd2.sandbox.tpp.rest.server.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class TppSwaggerConfig {
    private static final String API_KEY = "apiKey";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final BuildProperties buildProperties;

    @Bean
    public GroupedOpenApi tppApi() {
        return GroupedOpenApi.builder()
                   .group("TPP-API")
                   .packagesToScan("de.adorsys.psd2.sandbox.tpp")
                   .build();
    }

    @Bean
    public OpenAPI metaData() {

        Contact contact = new Contact()
                              .name("Adorsys")
                              .email("psd2@adorsys.com")
                              .url("https://www.adorsys.de");

        return new OpenAPI()
                   .info(new Info()
                             .title("TPP backend application")
                             .description("TPP backend application of PSD2 ModelBank Environment")
                             .contact(contact)
                             .version(buildProperties.getVersion() + " " + buildProperties.get("build.number"))
                             .license(new License()
                                          .name("AGPL version 3.0")
                                          .url("https://www.gnu.org/licenses/agpl-3.0.txt")))
                   .components(new Components()
                                   .addSecuritySchemes(API_KEY, new SecurityScheme()
                                                                    .type(SecurityScheme.Type.APIKEY)
                                                                    .in(SecurityScheme.In.HEADER)
                                                                    .name(AUTHORIZATION_HEADER)));
    }
}
