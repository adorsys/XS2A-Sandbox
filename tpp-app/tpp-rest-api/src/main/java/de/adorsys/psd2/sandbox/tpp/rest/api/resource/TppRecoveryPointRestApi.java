package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "TPP Revert Point API")
public interface TppRecoveryPointRestApi {
    String BASE_PATH = "/tpp/recovery";

    @ApiOperation(value = "Get recovery point by id", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/point/{id}")
    ResponseEntity<RecoveryPointTO> point(@PathVariable("id") Long id);

    @ApiOperation(value = "Get all recovery points for TPP", authorizations = @Authorization(value = "apiKey"))
    @GetMapping("/points")
    ResponseEntity<List<RecoveryPointTO>> points();

    @ApiOperation(value = "Create a new recovery point", authorizations = @Authorization(value = "apiKey"))
    @PostMapping("/point")
    ResponseEntity<Void> createPoint(@RequestBody RecoveryPointTO recoveryPoint);

    @ApiOperation(value = "Remove existing recovery point by id", authorizations = @Authorization(value = "apiKey"))
    @DeleteMapping("/point/{id}")
    ResponseEntity<Void> deletePoint(@PathVariable("id") Long id);
}
