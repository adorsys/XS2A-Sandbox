package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAPaymentResponseTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Api(value = ObaAuthorizationApi.BASE_PATH, tags = "Online Banking PIS Cancellation.")
public interface ObaCancellationApi {
    String BASE_PATH = "/api/v1/payment/cancellation";

    @PostMapping
    @ApiOperation(value = "Initiate payment cancellation process", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<SCAPaymentResponseTO> initCancellation(@RequestParam String paymentId);

    @PostMapping("/sca")
    @ApiOperation(value = "Select Sca Method for payment cancellation", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<SCAPaymentResponseTO> selectSca(@RequestParam String paymentId, @RequestParam String cancellationId, @RequestParam String scaMethodId);

    @PutMapping("/confirmation")
    @ApiOperation(value = "Confirm payment cancellation with TAN", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Void> validateTAN(@RequestParam String paymentId, @RequestParam String cancellationId, @RequestParam String authCode);
}
