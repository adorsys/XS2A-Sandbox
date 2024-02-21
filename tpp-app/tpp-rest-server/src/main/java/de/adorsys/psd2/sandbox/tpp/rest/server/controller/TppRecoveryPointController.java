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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRecoveryPointRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppRecoveryPointRestApi.BASE_PATH)
public class TppRecoveryPointController implements TppRecoveryPointRestApi {
    private final DataRestClient dataRestClient;

    @Override
    public ResponseEntity<RecoveryPointTO> point(Long id) {
        return dataRestClient.getPoint(id);
    }

    @Override
    public ResponseEntity<List<RecoveryPointTO>> points() {
        return dataRestClient.getAllPoints();
    }

    @Override
    public ResponseEntity<Void> createPoint(RecoveryPointTO recoveryPoint) {
        return dataRestClient.createPoint(recoveryPoint);
    }

    @Override
    public ResponseEntity<Void> deletePoint(Long id) {
        return dataRestClient.deletePoint(id);
    }
}
