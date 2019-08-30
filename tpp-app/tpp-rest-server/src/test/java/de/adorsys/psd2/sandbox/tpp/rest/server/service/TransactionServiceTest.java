package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.MockTransactionsStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.MockTransactionDataConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {
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
    public void uploadUserTransaction() throws IOException {
        UserTransaction tr = parseService1.convertFileToTargetObject(resolveMultipartFile("transactions_template.csv"), UserTransaction.class).get(0);
        when(parseService.convertFileToTargetObject(any(), any())).thenReturn(Collections.singletonList(tr));
        when(transactionDataConverter.toLedgersMockTransactions((anyList()))).thenReturn(converter.toLedgersMockTransactions(Collections.singletonList(tr)));
        when(transactionsStaffRestClient.transactions(anyList())).thenReturn(ResponseEntity.ok(new HashMap<String, String>()));

        Map<String, String> result = transactionService.uploadUserTransaction(resolveMultipartFile("transactions_template.csv"));
        assertThat(result).isEmpty();
        assertThat(result).isEqualTo(new HashMap<String, String>());
    }


    private MultipartFile resolveMultipartFile(String fileName) throws IOException {
        Resource resource = resourceLoader.getResource(fileName);
        return new MockMultipartFile("file", resource.getFile().getName(), "text/plain", resource.getInputStream());
    }
}
