package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAisApi;
import de.adorsys.ledgers.oba.rest.server.service.AisService;
import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ObaAisController.BASE_PATH)
@RequiredArgsConstructor
public class ObaAisController implements ObaAisApi {
    private final AisService aisService;

    @Override
    public ResponseEntity<List<AisAccountConsent>> consents(String userLogin) {
        return ResponseEntity.ok(aisService.getListOfConsents(userLogin));
    }

}
