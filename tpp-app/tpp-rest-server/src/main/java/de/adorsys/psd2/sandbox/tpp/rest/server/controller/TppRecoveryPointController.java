package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.api.domain.general.RecoveryPointTO;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRecoveryPointRestApi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppRecoveryPointRestApi.BASE_PATH)
public class TppRecoveryPointController implements TppRecoveryPointRestApi {
    private final DataRestClient dataRestClient;

    @Override
    public ResponseEntity<RecoveryPointTO> point(Long id) {
        return dataRestClient.getPoint(id);
    }

    @Override
    public ResponseEntity<List<RecoveryPointTO>> points() {
        return dataRestClient.getAllPoints();
    }

    @Override
    public ResponseEntity<Void> createPoint(RecoveryPointTO recoveryPoint) {
        return dataRestClient.createPoint(recoveryPoint);
    }

    @Override
    public ResponseEntity<Void> deletePoint(Long id) {
        return dataRestClient.deletePoint(id);
    }
}
