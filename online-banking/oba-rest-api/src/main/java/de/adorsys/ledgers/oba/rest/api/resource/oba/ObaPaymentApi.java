package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api(value = ObaPaymentApi.BASE_PATH, tags = "Online Banking Payments")
public interface ObaPaymentApi {
    String BASE_PATH = "/api/v1/payments";

    /**
     * @param userLogin login of current user
     * @return List of periodic payments for user
     */
    @GetMapping(path = "/{userLogin}/periodic")
    @ApiOperation(value = "Get List of periodic payments for user", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<ObaCmsPeriodicPayment>> periodicPayments(@PathVariable("userLogin") String userLogin);
}
