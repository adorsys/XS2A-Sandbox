package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class ParseServiceTest {
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ParseService parseService = new ParseService(resourceLoader);

    @Test
    public void getDataFromFile_Consents() throws IOException {
        //given
        MultipartFile multipartFile = resolveMultipartFile("consents_template.yml");

        //when
        Optional<List<AisConsent>> data = parseService.getDataFromFile(multipartFile, new TypeReference<List<AisConsent>>() {
        });

        //then
        assertThat(data.isPresent()).isTrue();
        data.get().forEach(this::assertNoNullFields);
    }

    @Test
    public void getDefaultData() {
        //when
        Optional<DataPayload> data = parseService.getDefaultData();

        //then
        assertThat(data.isPresent()).isTrue();
    }

    @Test
    public void convertMultiPartToFile() throws IOException {
        //when
        List<UserTransaction> transactions = parseService.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class);

        //then
        assertThat(transactions).isNotEmpty();
        assertThat(transactions.get(0)).isEqualToComparingFieldByField(buildSingleUserTransaction());
    }

    @Test(expected = TppException.class)
    public void convertMultiPartToFile_fileNull() {
        //when
        parseService.convertFileToTargetObject(null, UserTransaction.class);
    }

    @Test
    public void getDataFromFile_DataPayload() throws IOException {
        //given
        MultipartFile multipartFile = resolveMultipartFile("data_payload_template.yml");

        //when
        Optional<DataPayload> data = parseService.getDataFromFile(multipartFile, new TypeReference<DataPayload>() {
        });

        //then
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
