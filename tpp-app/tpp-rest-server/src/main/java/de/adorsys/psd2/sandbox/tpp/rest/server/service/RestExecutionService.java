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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import de.adorsys.ledgers.middleware.api.domain.general.RevertRequestTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UploadedDataTO;
import de.adorsys.ledgers.middleware.client.mappers.PaymentMapperTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsDbNativeService;
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

import static java.util.Objects.requireNonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestExecutionService {
    private final DataRestClient dataRestClient;
    private final BalanceMapper balanceMapper;
    private final PaymentMapperTO paymentTOMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final CmsDbNativeService cmsDbNativeService;


    public void updateLedgers(DataPayload payload) {
        if (!payload.isValidPayload()) {
            throw new TppException("Payload data is invalid", 400);
        }
        dataRestClient.uploadData(initialiseDataSets(payload));
    }

    @SuppressWarnings("PMD:PrematureDeclaration")
    public void revert(RevertRequestTO revertRequest) {
        // If users are present within this branch - get their logins (PSU IDs) and clean the ledgers and CMS DB with logins and timestamp.
        List<String> logins = userMgmtStaffRestClient.getBranchUserLogins().getBody();
        RecoveryPointTO point = dataRestClient.getPoint(revertRequest.getRecoveryPointId()).getBody();

        userMgmtStaffRestClient.revertDatabase(revertRequest);
        cmsDbNativeService.revertDatabase(logins, requireNonNull(point).getRollBackTime());
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
        return paymentTOMapper.toAbstractPayment(paymentString, "SINGLE", payment.getPaymentProduct());
    }

    private Map<String, AccountBalanceTO> toAccountBalanceTO(Map<String, AccountBalance> balancesByIban) {
        return balancesByIban.entrySet().stream()
                   .collect(Collectors.toMap(Map.Entry::getKey, e -> balanceMapper.toAccountBalanceTO(e.getValue())));
    }
}
