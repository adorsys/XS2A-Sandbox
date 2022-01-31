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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.ScaVerificationRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppEmailVerificationRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppEmailVerificationRestApi.BASE_PATH)
public class TppEmailVerificationController implements TppEmailVerificationRestApi {
    private final ScaVerificationRestClient scaVerificationRestClient;

    @Override
    public ResponseEntity<Void> sendEmailVerification(String email) {
        return scaVerificationRestClient.sendEmailVerification(email);
    }

    @Override
    public ResponseEntity<Void> confirmVerificationToken(String token) {
        return scaVerificationRestClient.confirmVerificationToken(token);
    }
}
