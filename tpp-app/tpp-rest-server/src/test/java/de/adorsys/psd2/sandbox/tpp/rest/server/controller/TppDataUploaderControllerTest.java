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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppDataUploaderControllerTest {
    private static final String TPP_ID = "DE_12345678";
    private static final String USER_IBAN = "DE89000000115555555555";
    private static final Currency CURRENCY = Currency.getInstance("EUR");
    private static final String FILE_NAME = "transactions_template.csv";

    private final ResourceLoader resourceLoader = new DefaultResourceLoader();

    @InjectMocks
    private TppDataUploaderController uploaderController;
    @Mock
    private RestExecutionService restExecutionService;
    @Mock
    private ParseService parseService;
    @Mock
    private TestsDataGenerationService generationService;
    @Mock
    private IbanGenerationService ibanGenerationService;
    @Mock
    private TransactionService transactionService;
    @Mock
    private DownloadResourceService downloadResourceService;

    private static final ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

    @Test
    void generateData() throws JsonProcessingException {
        // Given
        when(generationService.generate(false, CURRENCY)).thenReturn(getBytes());

        // When
        ResponseEntity<Resource> response = uploaderController.generateData(false, "EUR");

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void generateIban() {
        // Given
        when(ibanGenerationService.generateNextIban(TPP_ID)).thenReturn(USER_IBAN);

        // When
        ResponseEntity<String> response = uploaderController.generateIban(TPP_ID);

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void uploadData() throws IOException {
        // Given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.of(getPayload()));

        // When
        ResponseEntity<String> response = uploaderController.uploadData(resolveMultipartFile("transactions_template.csv"));

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void uploadData_couldNotParseData() throws IOException {
        // Given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.empty());
        MultipartFile file = resolveMultipartFile("transactions_template.csv");
        // Then
        assertThrows(TppException.class, () -> uploaderController.uploadData(file));
    }

    @Test
    void uploadTransactions() throws IOException {
        // Given
        when(transactionService.uploadUserTransaction(any())).thenReturn(Collections.emptyMap());

        // When
        ResponseEntity<Map<String, String>> response = uploaderController.uploadTransactions(resolveMultipartFile("transactions_template.csv"));

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        assertEquals(response.getBody(), new HashMap<String, String>());
    }

    @Test
    void downloadTransactionTemplate() {
        // Given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(resourceLoader.getResource(FILE_NAME));

        // When
        ResponseEntity<Resource> response = uploaderController.downloadTransactionTemplate();

        // Then
        assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }

    private byte[] getBytes() throws JsonProcessingException {
        return objectMapper.writeValueAsBytes(getPayload());
    }

    private DataPayload getPayload() {
        List<UserTO> users = Collections.singletonList(new UserTO("login", "email", "pin"));
        List<AccountDetailsTO> accounts = Collections.singletonList(new AccountDetailsTO());
        List<AccountBalance> balances = Collections.singletonList(new AccountBalance());
        List<PaymentTO> payments = Collections.singletonList(new PaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }
}
