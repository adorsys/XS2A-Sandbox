package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ParseServiceTest {
    @InjectMocks
    private ParseService parseService;

    @Mock
    private ObjectMapper objectMapper;

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

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
        FieldSetter.setField(parseService, parseService.getClass().getDeclaredField("resourceLoader"), resourceLoader);

        // When
        Optional<DataPayload> data = parseService.getDefaultData();

        // Then
        assertTrue(data.isPresent());
    }

    @Test
    void generateFileByPayload() {
        // When
        byte[] result = parseService.generateFileByPayload(new DataPayload());

        // Then
        assertThat(result).isNotEmpty();
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
        Resource resource = resourceLoader.getResource(fileName);
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
