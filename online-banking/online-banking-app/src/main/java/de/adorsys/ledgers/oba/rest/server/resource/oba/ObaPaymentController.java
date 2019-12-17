package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaPaymentApi;
import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import de.adorsys.ledgers.oba.service.api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ObaPaymentController.BASE_PATH)
@RequiredArgsConstructor
public class ObaPaymentController implements ObaPaymentApi {
    private final PaymentService paymentService;

    @PreAuthorize("#userLogin == authentication.principal.login")
    @Override
    public ResponseEntity<List<ObaCmsPeriodicPayment>> periodicPayments(String userLogin) {
        return ResponseEntity.ok(paymentService.getPeriodicPayments(userLogin));
    }
}
