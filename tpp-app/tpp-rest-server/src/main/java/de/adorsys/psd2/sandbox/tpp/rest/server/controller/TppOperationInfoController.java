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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppOperationInfoRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TppOperationInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(TppOperationInfoRestApi.BASE_PATH)
@RequiredArgsConstructor
public class TppOperationInfoController implements TppOperationInfoRestApi {
    private final TppOperationInfoService infoService;

    @Override
    public ResponseEntity<List<OperationInfo>> getAllOperations(OperationType operationType) {
        return ResponseEntity.ok(infoService.getOperationsByTypeAndTppId(operationType));
    }

    @Override
    public ResponseEntity<OperationInfo> addOperationInfo(OperationInfo operationInfo) {
        return ResponseEntity.ok(infoService.createInfo(operationInfo));
    }

    @Override
    public ResponseEntity<Void> deleteOperationInfo(Long operationInfoId) {
        infoService.deleteInfo(operationInfoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
