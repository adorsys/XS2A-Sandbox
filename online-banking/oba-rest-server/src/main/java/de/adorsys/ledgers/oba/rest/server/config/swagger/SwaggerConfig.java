/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.config.swagger;

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
public class SwaggerConfig {
    private static final String API_KEY = "apiKey";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final BuildProperties buildProperties;

    @Bean
    public GroupedOpenApi obaApi() {
        return GroupedOpenApi.builder()
                   .group("OBA-API")
                   .packagesToScan("de.adorsys.ledgers.oba")
                   .build();
    }

    @Bean
    public OpenAPI metaData() {

        Contact contact = new Contact()
                              .name("Adorsys")
                              .email("sales@adorsys.com")
                              .url("https://www.adorsys.de");

        return new OpenAPI()
                   .info(new Info()
                             .title("Online banking")
                             .description("Implementation of backend for online banking UI. "
                                              + "We have 3 preloaded users in Ledgers: <b>marion.mueller</b>, <b>anton.brueckner</b>, <b>max.musterman</b> all with the PIN <b>12345</b>. "
                                              + "You can use the Keycloak API: <b>{keycloak.url}/realms/ledgers/protocol/openid-connect/token</b> to gain an access token. Then use the access token with the prefix 'Bearer ' to authorize on this UI."
                             )
                             .contact(contact)
                             .version(buildProperties.getVersion() + " " + buildProperties.get("build.number"))
                             .license(new License().name("AGPL version 3.0")
                                          .url("https://www.gnu.org/licenses/agpl-3.0.txt")))
                   .components(new Components()
                                   .addSecuritySchemes(API_KEY, new SecurityScheme()
                                                                    .type(SecurityScheme.Type.APIKEY)
                                                                    .in(SecurityScheme.In.HEADER)
                                                                    .name(AUTHORIZATION_HEADER)));
    }
}
