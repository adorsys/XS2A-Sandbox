package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UploadedDataTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.BalanceMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestExecutionService {
    private final DataRestClient dataRestClient;
    private final BalanceMapper balanceMapper;
    private final PaymentMapperTO paymentTOMapper;

    public void updateLedgers(DataPayload payload) {
        if (!payload.isValidPayload()) {
            throw new TppException("Payload data is invalid", 400);
        }
        dataRestClient.uploadData(initialiseDataSets(payload));
    }

    private UploadedDataTO initialiseDataSets(DataPayload payload) {
        List<PaymentTO> paymentTOs = payload.getPayments().stream()
                                            .map(this::performMapping)
                                            .collect(Collectors.toList());
        return new UploadedDataTO(payload.getUsers(),
            payload.getAccountByIban(),
            toAccountBalanceTO(payload.getBalancesByIban()),
            paymentTOs,
            payload.isGeneratePayments(),
            payload.getBranch());
    }

    @SneakyThrows
    private PaymentTO performMapping(PaymentTO payment) {
        String paymentString = paymentTOMapper.getMapper().writeValueAsString(payment);
        return paymentTOMapper.toAbstractPayment(paymentString, "SINGLE", payment.getPaymentProduct()); //.getPaymentProduct().getValue()
    }

    private Map<String, AccountBalanceTO> toAccountBalanceTO(Map<String, AccountBalance> balancesByIban) {
        return balancesByIban.entrySet().stream()
                   .collect(Collectors.toMap(Map.Entry::getKey, e -> balanceMapper.toAccountBalanceTO(e.getValue())));
    }
}
