/*
 * Copyright 2018-2025 adorsys GmbH & Co KG
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

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TppOperationInfoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppOperationInfoControllerTest {

    @InjectMocks
    private TppOperationInfoController tppOperationInfoController;

    @Mock
    private TppOperationInfoService tppOperationInfoService;

    @Test
    void getAllOperations() {
        // Given
        when(tppOperationInfoService.getOperationsByTypeAndTppId(OperationType.CONSENT))
            .thenReturn(Collections.singletonList(new OperationInfo()));
        // When
        ResponseEntity<List<OperationInfo>> actual = tppOperationInfoController.getAllOperations(OperationType.CONSENT);

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }

    @Test
    void addOperationInfo() {
        // Given
        when(tppOperationInfoService.createInfo(any(OperationInfo.class)))
            .thenReturn((new OperationInfo()));
        // When
        ResponseEntity<OperationInfo> actual = tppOperationInfoController.addOperationInfo(new OperationInfo());

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }

    @Test
    void deleteOperationInfo() {
        // When
        ResponseEntity<Void> actual = tppOperationInfoController.deleteOperationInfo(1L);

        // Then
        assertNotNull(actual);
        assertTrue(actual.getStatusCode().is2xxSuccessful());
    }
}
