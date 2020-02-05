package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
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
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppDataUploaderControllerTest {
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
    public void generateData() throws JsonProcessingException {
        //given
        when(generationService.generate(false, CURRENCY)).thenReturn(getBytes());

        //when
        ResponseEntity<Resource> response = uploaderController.generateData(false, "EUR");

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    public void generateIban() {
        //given
        when(ibanGenerationService.generateNextIban()).thenReturn(USER_IBAN);

        //when
        ResponseEntity<String> response = uploaderController.generateIban();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    public void uploadData() throws IOException {
        //given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.of(getPayload()));

        //when
        ResponseEntity<String> response = uploaderController.uploadData(resolveMultipartFile("transactions_template.csv"));

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test(expected = TppException.class)
    public void uploadData_couldNotParseData() throws IOException {
        //given
        when(parseService.getDataFromFile(any(), any())).thenReturn(Optional.empty());

        //when
        uploaderController.uploadData(resolveMultipartFile("transactions_template.csv"));
    }

    @Test
    public void uploadTransactions() throws IOException {
        //given
        when(transactionService.uploadUserTransaction(any())).thenReturn(Collections.EMPTY_MAP);

        //when
        ResponseEntity<Map<String, String>> response = uploaderController.uploadTransactions(resolveMultipartFile("transactions_template.csv"));

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertTrue(Objects.requireNonNull(response.getBody()).isEmpty());
        assertEquals(response.getBody(), new HashMap<String, String>());
    }

    @Test
    public void downloadTransactionTemplate(){
        //given
        when(downloadResourceService.getResourceByTemplate(any())).thenReturn(resourceLoader.getResource(FILE_NAME));

        //when
        ResponseEntity<Resource> response = uploaderController.downloadTransactionTemplate();

        //then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
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
        List<SinglePaymentTO> payments = Collections.singletonList(new SinglePaymentTO());
        return new DataPayload(users, accounts, balances, payments, false, TPP_ID, new HashMap<>());
    }
}
