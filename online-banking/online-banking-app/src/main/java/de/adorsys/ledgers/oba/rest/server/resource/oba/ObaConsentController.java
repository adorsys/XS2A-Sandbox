package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaConsentApi;
import de.adorsys.ledgers.oba.rest.server.service.ConsentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
@RestController
@RequestMapping(ObaConsentController.BASE_PATH)
@RequiredArgsConstructor
public class ObaConsentController implements ObaConsentApi {
    private final ConsentService consentService;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<ObaAisConsent>> consents(String userLogin) {
        return ResponseEntity.ok(consentService.getListOfConsents(userLogin));
    }

    @Override
    public ResponseEntity<Boolean> revokeConsent(String consentId) {
        return ResponseEntity.ok(consentService.revokeConsent(consentId));
    }

    @Override
    public ResponseEntity<Void> confirm(String userLogin, String consentId, String authorizationId, String tan) {
        consentService.confirmAisConsentDecoupled(userLogin, consentId, authorizationId, tan);
        return new ResponseEntity<>(ACCEPTED);
    }
}
