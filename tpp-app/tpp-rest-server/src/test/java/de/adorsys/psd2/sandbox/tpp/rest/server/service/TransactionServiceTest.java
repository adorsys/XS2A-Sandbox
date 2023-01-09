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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.MockTransactionsStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.MockTransactionDataConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private ParseService parseService1 = new ParseService(resourceLoader);
    private MockTransactionDataConverter converter = Mappers.getMapper(MockTransactionDataConverter.class);

    @InjectMocks
    TransactionService transactionService;
    @Mock
    private ParseService parseService = new ParseService(resourceLoader);
    @Mock
    private MockTransactionsStaffRestClient transactionsStaffRestClient;
    @Mock
    private MockTransactionDataConverter transactionDataConverter;

    @Test
    void uploadUserTransaction() throws IOException {
        // Given
        UserTransaction tr = parseService1.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class).get(0);
        when(parseService.convertFileToTargetObject(any(), any())).thenReturn(Collections.singletonList(tr));
        when(transactionDataConverter.toLedgersMockTransactions((anyList()))).thenReturn(converter.toLedgersMockTransactions(Collections.singletonList(tr)));
        when(transactionsStaffRestClient.transactions(anyList())).thenReturn(ResponseEntity.ok(new HashMap<>()));

        // When
        Map<String, String> result = transactionService.uploadUserTransaction(resolveMultipartFile("transactions_template.csv"));

        // Then
        assertTrue(result.isEmpty());
        assertEquals(new HashMap<String, String>(), result);
    }

    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }
}
