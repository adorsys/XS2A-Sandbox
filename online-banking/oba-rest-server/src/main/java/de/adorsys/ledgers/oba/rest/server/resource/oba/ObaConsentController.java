package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaConsentApi;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.rest.server.resource.AuthUtils;
import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.ledgers.oba.service.api.domain.ObaAisConsent;
import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.oba.service.api.service.ConsentService;
import de.adorsys.ledgers.oba.service.api.service.TppInfoCmsService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.ledgers.oba.rest.api.resource.oba.ObaConsentApi.BASE_PATH;
import static org.springframework.http.HttpStatus.ACCEPTED;

@Slf4j
@RestController
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
public class ObaConsentController implements ObaConsentApi {
    private final ConsentService consentService;
    private final MiddlewareAuthentication auth;
    private final TppInfoCmsService tppInfoCmsService;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<ObaAisConsent>> consents(String userLogin) {
        return ResponseEntity.ok(consentService.getListOfConsents(userLogin));
    }

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<CustomPageImpl<ObaAisConsent>> consentsPaged(String userLogin, int page, int size) {
        return ResponseEntity.ok(consentService.getListOfConsentsPaged(userLogin, page, size));
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

    @Override
    public ResponseEntity<Void> createPiis(CreatePiisConsentRequestTO request) {
        consentService.createPiisConsent(request, AuthUtils.psuId(auth));
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<TppInfoTO>> tpps() {
        return ResponseEntity.ok(tppInfoCmsService.getTpps());
    }
}
