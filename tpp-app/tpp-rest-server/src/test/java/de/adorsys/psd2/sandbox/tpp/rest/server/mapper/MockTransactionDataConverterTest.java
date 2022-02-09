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

package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.MockBookingDetails;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.ParseService;
import org.junit.jupiter.api.Test;
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
import static org.junit.jupiter.api.Assertions.assertEquals;

class MockTransactionDataConverterTest {
    private final MockTransactionDataConverter converter = Mappers.getMapper(MockTransactionDataConverter.class);
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private final ParseService parseService = new ParseService(resourceLoader);

    @Test
    void toLocalDateTest() {
        // Given
        String input = "01.01.2019";

        // When
        LocalDate result = converter.toLocalDate(input);
        assertEquals(LocalDate.of(2019, 01, 01), result);
    }

    @Test
    void toLedgersMockTransactionTest() throws IOException {
        // Given
        List<UserTransaction> input = parseService.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class);
        MockBookingDetails expected = getExpectedMockTransaction();

        // When
        MockBookingDetails result = converter.toLedgersMockTransaction(input.get(0));

        // Then
        assertEquals(expected, result);
    }

    @Test
    void toLedgersMockTransactionsTest() throws IOException {
        // Given
        MockBookingDetails expected = getExpectedMockTransaction();
        List<UserTransaction> input = getListWith2EqualTransactions();

        // When
        List<MockBookingDetails> result = converter.toLedgersMockTransactions(input);
        assertThat(result.size()).isEqualTo(2);

        // Then
        result.forEach(r -> assertEquals(expected, r));
    }

    private List<UserTransaction> getListWith2EqualTransactions() throws IOException {
        List<UserTransaction> transactionList = parseService.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class);
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
