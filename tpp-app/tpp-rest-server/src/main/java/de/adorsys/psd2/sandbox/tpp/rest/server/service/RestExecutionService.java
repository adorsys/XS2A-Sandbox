package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.um.UploadedDataTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.BalanceMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestExecutionService {
    private final DataRestClient dataRestClient;
    private final BalanceMapper balanceMapper;

    public void updateLedgers(DataPayload payload) {
        if (!payload.isValidPayload()) {
            throw new TppException("Payload data is invalid", 400);
        }
        dataRestClient.uploadData(initialiseDataSets(payload));
    }

    private UploadedDataTO initialiseDataSets(DataPayload payload) {
        return new UploadedDataTO(payload.getUsers(),
                                  payload.getAccountByIban(),
                                  toAccountBalanceTO(payload.getBalancesByIban()),
                                  payload.getPayments(),
                                  payload.isGeneratePayments(),
                                  payload.getBranch());
    }

    private Map<String, AccountBalanceTO> toAccountBalanceTO(Map<String, AccountBalance> balancesByIban) {
        return balancesByIban.entrySet().stream()
                   .collect(Collectors.toMap(Map.Entry::getKey, e -> balanceMapper.toAccountBalanceTO(e.getValue())));
    }
}
