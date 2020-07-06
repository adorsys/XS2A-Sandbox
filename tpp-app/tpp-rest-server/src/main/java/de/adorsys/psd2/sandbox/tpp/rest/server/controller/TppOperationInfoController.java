package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppOperationInfoRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TppOperationInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(TppOperationInfoRestApi.BASE_PATH)
@RequiredArgsConstructor
public class TppOperationInfoController implements TppOperationInfoRestApi {
    private final TppOperationInfoService infoService;

    @Override
    public ResponseEntity<List<OperationInfo>> getAllOperations(OperationType operationType) {
        return ResponseEntity.ok(infoService.getOperationsByTypeAndTppId(operationType));
    }

    @Override
    public ResponseEntity<OperationInfo> addOperationInfo(OperationInfo operationInfo) {
        return ResponseEntity.ok(infoService.createInfo(operationInfo));
    }

    @Override
    public ResponseEntity<Void> deleteOperationInfo(Long operationInfoId) {
        infoService.deleteInfo(operationInfoId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
