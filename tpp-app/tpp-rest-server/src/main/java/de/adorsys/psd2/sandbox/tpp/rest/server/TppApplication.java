/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.tpp.rest.server;

import de.adorsys.ledgers.keycloak.client.KeycloakClientConfiguration;
import de.adorsys.ledgers.middleware.client.EnableLedgersMiddlewareRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.config.TppFeignConfig;
import org.adorsys.ledgers.consent.aspsp.rest.client.CmsAspspPiisClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPiisClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableLedgersMiddlewareRestClient
@Import(KeycloakClientConfiguration.class)
@EnableFeignClients(basePackageClasses = {UserMgmtStaffRestClient.class, UserMgmtRestClient.class, CmsAspspPiisClient.class, CmsPsuPiisClient.class}, defaultConfiguration = TppFeignConfig.class)
@SpringBootApplication(scanBasePackages = {"de.adorsys.psd2.mapper", "de.adorsys.psd2.sandbox.tpp.rest.server"})
public class TppApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TppApplication.class).run(args);
    }
}
