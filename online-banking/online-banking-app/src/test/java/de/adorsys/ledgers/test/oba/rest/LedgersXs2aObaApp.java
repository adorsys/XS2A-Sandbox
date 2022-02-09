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

package de.adorsys.ledgers.test.oba.rest;

import de.adorsys.ledgers.middleware.client.EnableLedgersMiddlewareRestClient;
import de.adorsys.ledgers.middleware.client.rest.AccountRestClient;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuPisClient;
import org.adorsys.ledgers.consent.xs2a.rest.client.AspspConsentDataClient;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackageClasses = {AccountRestClient.class, CmsPsuPisClient.class, AspspConsentDataClient.class})
@SpringBootApplication
@EnableLedgersMiddlewareRestClient
@ComponentScan(basePackages = {"de.adorsys.ledgers.oba", "de.adorsys.psd2.mapper"})
public class LedgersXs2aObaApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(LedgersXs2aObaApp.class).run(args);
    }
}
