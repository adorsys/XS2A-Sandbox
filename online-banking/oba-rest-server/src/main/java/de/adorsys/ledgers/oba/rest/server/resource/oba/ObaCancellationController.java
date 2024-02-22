/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.StartScaOprTO;
import de.adorsys.ledgers.middleware.client.rest.AuthRequestInterceptor;
import de.adorsys.ledgers.middleware.client.rest.OperationInitiationRestClient;
import de.adorsys.ledgers.middleware.client.rest.RedirectScaRestClient;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaCancellationApi;
import de.adorsys.psd2.consent.psu.api.CmsPsuPisService;
import de.adorsys.psd2.xs2a.core.pis.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.EnumSet;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.EXEMPTED;
import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.FINALISED;
import static de.adorsys.psd2.consent.psu.api.config.CmsPsuApiDefaultValue.DEFAULT_SERVICE_INSTANCE_ID;
import static java.util.Objects.requireNonNull;

@Slf4j
@RestController
@RequestMapping(ObaCancellationApi.BASE_PATH)
@RequiredArgsConstructor
public class ObaCancellationController implements ObaCancellationApi {
    private final CmsPsuPisService cmsPsuPisService;
    private final OperationInitiationRestClient operationInitiationRestClient;
    private final RedirectScaRestClient redirectScaRestClient;
    private final AuthRequestInterceptor auth;

    @Override
    public ResponseEntity<GlobalScaResponseTO> initCancellation(String paymentId) {
        GlobalScaResponseTO response = operationInitiationRestClient.initiatePmtCancellation(paymentId).getBody();
        HttpStatus status = resolveStatus(paymentId, requireNonNull(response));
        return new ResponseEntity<>(response, status);
    }

    @Override
    public ResponseEntity<GlobalScaResponseTO> selectSca(String paymentId, String cancellationId, String scaMethodId) {
        StartScaOprTO opr = new StartScaOprTO(paymentId, null, cancellationId, OpTypeTO.CANCEL_PAYMENT);
        redirectScaRestClient.startSca(opr);
        return ResponseEntity.ok(requireNonNull(redirectScaRestClient.selectMethod(cancellationId, scaMethodId).getBody()));
    }

    @Override
    public ResponseEntity<Void> validateTAN(String paymentId, String cancellationId, String authCode) {
        GlobalScaResponseTO validateScaCode = redirectScaRestClient.validateScaCode(cancellationId, authCode).getBody();
        auth.setAccessToken(requireNonNull(validateScaCode).getBearerToken().getAccess_token());
        GlobalScaResponseTO response = operationInitiationRestClient.execution(OpTypeTO.CANCEL_PAYMENT, paymentId).getBody();
        HttpStatus status = resolveStatus(paymentId, requireNonNull(response));
        return new ResponseEntity<>(status);
    }

    private HttpStatus resolveStatus(String paymentId, GlobalScaResponseTO globalScaResponseTO) {
        if (EnumSet.of(EXEMPTED, FINALISED).contains(globalScaResponseTO.getScaStatus())) {
            return cmsPsuPisService.updatePaymentStatus(paymentId, TransactionStatus.CANC, DEFAULT_SERVICE_INSTANCE_ID)
                       ? HttpStatus.NO_CONTENT
                       : HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
