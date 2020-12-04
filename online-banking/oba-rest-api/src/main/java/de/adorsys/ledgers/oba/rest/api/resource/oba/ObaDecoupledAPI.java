package de.adorsys.ledgers.oba.rest.api.resource.oba;

import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Api(value = ObaDecoupledAPI.BASE_PATH, tags = "Online Banking Decoupled")
public interface ObaDecoupledAPI {
    String BASE_PATH = "/api/v1/decoupled";

    @ApiOperation(value = "Confirm/Cancel Decoupled operation", authorizations = @Authorization(value = "apiKey"))
    @PostMapping(path = "/execute")
    ResponseEntity<Boolean> decoupled(@RequestBody DecoupledConfRequest message);

    @ApiOperation(value = "Send decoupled notification to users device", authorizations = @Authorization(value = "apiKey"))
    @PostMapping(path = "/message")
    ResponseEntity<Boolean> sendNotification(@RequestBody DecoupledConfRequest message);
}
