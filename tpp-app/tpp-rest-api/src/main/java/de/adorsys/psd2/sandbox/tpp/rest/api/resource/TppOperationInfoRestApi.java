package de.adorsys.psd2.sandbox.tpp.rest.api.resource;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "TPP Operation info management")
public interface TppOperationInfoRestApi {
    String BASE_PATH = "/tpp/operation";

    @GetMapping
    @ApiOperation(value = "Retrieves all Operation infos for current TPP", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<List<OperationInfo>> getAllOperations(@RequestParam(value = "operationType", required = false) OperationType operationType);

    @PostMapping
    @ApiOperation(value = "Adds new Operation info to database", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<OperationInfo> addOperationInfo(@RequestBody OperationInfo operationInfo);

    @DeleteMapping
    @ApiOperation(value = "Removes Operation info by id", authorizations = @Authorization(value = "apiKey"))
    ResponseEntity<Void> deleteOperationInfo(@RequestParam(value = "operationInfoId") Long operationInfoId);
}
