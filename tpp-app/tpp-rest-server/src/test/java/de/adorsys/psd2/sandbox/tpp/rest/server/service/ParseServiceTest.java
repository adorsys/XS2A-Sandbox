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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import de.adorsys.psd2.sandbox.cms.connector.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParseServiceTest {
    @InjectMocks
    private ParseService parseService;

    @Mock
    private ResourceLoader resourceLoader;

    private static final ResourceLoader LOCAL_LOADER = new DefaultResourceLoader();

    @Test
    void getDataFromFile_Consents() throws IOException {
        // Given
        MultipartFile multipartFile = resolveMultipartFile("consents_template.yml");

        // When
        Optional<List<AisConsent>> data = parseService.getDataFromFile(multipartFile, new TypeReference<>() {
        });

        // Then
        assertTrue(data.isPresent());
        data.get().forEach(this::assertNoNullFields);
    }

    @Test
    void getDataFromFile_Consents_fail() throws IOException {
        // Given
        MultipartFile multipartFile = resolveMultipartFile("consents_template_error.yml");

        // When
        Optional<List<AisConsent>> data = parseService.getDataFromFile(multipartFile, new TypeReference<>() {
        });

        // Then
        assertTrue(data.isEmpty());
    }

    @Test
    void getDefaultData() throws NoSuchFieldException {
        // Given
        ReflectionTestUtils.setField(parseService, "resourceLoader", LOCAL_LOADER);

        // When
        Optional<DataPayload> data = parseService.getDefaultData();

        // Then
        assertTrue(data.isPresent());
    }

    @Test
    void getDefaultData_error() {
        // Given
        when(resourceLoader.getResource(anyString())).thenReturn(new ByteArrayResource("TEST".getBytes()));

        // When
        Optional<DataPayload> data = parseService.getDefaultData();

        // Then
        assertFalse(data.isPresent());
    }

    @Test
    void generateFileByPayload() {
        // When
        byte[] result = parseService.generateFileByPayload(new DataPayload());

        // Then
        assertThat(result).isNotEmpty();
    }

    @Test
    void convertFileToTargetObject_failure() throws IOException {
        // When
        MockMultipartFile mock = mock(MockMultipartFile.class);
        when(mock.getInputStream()).thenThrow(IOException.class);

        // Then
        assertThrows(TppException.class, () -> parseService.convertFileToTargetObject(mock, UserTransaction.class));
    }

    @Test
    void convertMultiPartToFile() throws IOException {
        // When
        List<UserTransaction> transactions = parseService.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class);

        // Then
        assertThat(transactions).isNotEmpty();
        assertEquals(buildSingleUserTransaction(), transactions.get(0));
    }

    @Test
    void convertMultiPartToFile_fileNull() {
        // Then
        assertThrows(TppException.class, () -> parseService.convertFileToTargetObject(null, UserTransaction.class));
    }

    @Test
    void getDataFromFile_DataPayload() throws IOException {
        // Given
        MultipartFile multipartFile = resolveMultipartFile("data_payload_template.yml");

        // When
        Optional<DataPayload> data = parseService.getDataFromFile(multipartFile, new TypeReference<>() {
        });

        // Then
        validateDataPayload(data);
    }

    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = LOCAL_LOADER.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }

    private void validateDataPayload(Optional<DataPayload> data) {
        assertThat(data.isPresent()).isTrue();
        DataPayload payload = data.get();
        assertThat(payload.getBranch() == null).isTrue();
        assertThat(payload.getGeneratedIbans().size() == 0).isTrue();
        assertThat(payload.getUsers().size() == 1).isTrue();
        assertThat(payload.getAccounts().size() == 1).isTrue();
        assertThat(payload.getBalancesList().size() == 1).isTrue();
        assertThat(payload.getPayments().size() == 1).isTrue();
    }

    private void assertNoNullFields(AisConsent consent) {
        assertThat(consent).hasNoNullFieldsOrProperties();
        assertThat(consent.getPsuInfo()).hasNoNullFieldsOrProperties();
        assertThat(consent.getTppInfo()).hasNoNullFieldsOrProperties();
        consent.getAccess().getAccounts().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
        consent.getAccess().getBalances().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
        consent.getAccess().getTransactions().forEach(a -> assertThat(a).hasNoNullFieldsOrProperties());
    }

    private UserTransaction buildSingleUserTransaction() {
        UserTransaction transaction = new UserTransaction();
        transaction.setId("50405667");
        transaction.setPostingDate("30.01.2017");
        transaction.setValueDate("30.01.2017");
        transaction.setText("Lohn/Gehalt");
        transaction.setUsage("Gehalt Teambank");
        transaction.setPayer("Teambank Ag");
        transaction.setAccountNumber("7807800780");
        transaction.setBlzCode("VOHADE2HXXX");
        transaction.setAmount("2116.17");
        transaction.setCurrency("EUR");
        return transaction;
    }
}
