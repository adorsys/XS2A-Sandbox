package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaConsentApi;
import de.adorsys.ledgers.oba.rest.server.service.ConsentService;
import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ObaConsentController.BASE_PATH)
@RequiredArgsConstructor
public class ObaConsentController implements ObaConsentApi {
    private final ConsentService consentService;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<AisAccountConsent>> consents(String userLogin) {
        return ResponseEntity.ok(consentService.getListOfConsents(userLogin));
    }

}
