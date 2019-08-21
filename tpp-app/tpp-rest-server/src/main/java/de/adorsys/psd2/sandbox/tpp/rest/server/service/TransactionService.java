package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.MockTransactionsStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.MockTransactionDataConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final ParseService parseService;
    private final MockTransactionsStaffRestClient transactionsStaffRestClient;
    private final MockTransactionDataConverter transactionDataConverter;

    public Map<String, String> uploadUserTransaction(MultipartFile multipart) {
        log.info("parsing transaction data");
        List<UserTransaction> transactions = parseService.convertFileToTargetObject(multipart, UserTransaction.class);
        log.info("{} transactions parsed", transactions.size());
        Map<String, String> map = transactionsStaffRestClient.transactions(transactionDataConverter.toLedgersMockTransactions(transactions)).getBody();
        log.info("Call to Ledgers to create new transactions successful");
        return map;
    }
}
