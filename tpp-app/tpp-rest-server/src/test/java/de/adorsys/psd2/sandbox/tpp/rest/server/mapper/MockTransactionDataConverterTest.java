package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.deposit.api.domain.MockBookingDetails;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MockTransactionDataConverterTest {
    private final MockTransactionDataConverter converter = Mappers.getMapper(MockTransactionDataConverter.class);
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ParseService parseService = new ParseService(resourceLoader);

    @Test
    public void toLocalDateTest() {
        String input = "01.01.2019";
        LocalDate result = converter.toLocalDate(input);
        assertThat(result).isEqualTo(LocalDate.of(2019, 01, 01));
    }

    @Test
    public void toLedgersMockTransactionTest() throws IOException {
        List<UserTransaction> input = parseService.convertFileToTargetObject(resolveMultipartFile("team_bank_transaction.csv"), UserTransaction.class);
        MockBookingDetails expected = getExpectedMockTransaction();
        MockBookingDetails result = converter.toLedgersMockTransaction(input.get(0));
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    @Test
    public void toLedgersMockTransactionsTest() throws IOException {
        MockBookingDetails expected = getExpectedMockTransaction();
        List<UserTransaction> input = getListWith2EqualTransactions();
        List<MockBookingDetails> result = converter.toLedgersMockTransactions(input);
        assertThat(result.size()).isEqualTo(2);
        result.forEach(r -> assertThat(r).isEqualToComparingFieldByFieldRecursively(expected));
    }

    private List<UserTransaction> getListWith2EqualTransactions() throws IOException {
        List<UserTransaction> transactionList = parseService.convertFileToTargetObject(resolveMultipartFile("team_bank_transaction.csv"), UserTransaction.class);
        transactionList.add(transactionList.get(0));
        return transactionList;
    }

    private MockBookingDetails getExpectedMockTransaction() {
        MockBookingDetails details = new MockBookingDetails();
        details.setUserAccount("50405667");
        details.setBookingDate(LocalDate.of(2017, 1, 30));
        details.setValueDate(LocalDate.of(2017, 1, 30));
        details.setRemittance("Gehalt Teambank");
        details.setCrDrName("Teambank Ag");
        details.setOtherAccount("7807800780");
        details.setAmount(BigDecimal.valueOf(2116.17));
        details.setCurrency(Currency.getInstance("EUR"));
        return details;
    }


    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }
}
