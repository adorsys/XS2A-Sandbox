package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.psd2.sandbox.tpp.cms.api.domain.AisConsent;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppConsentRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.TppConsentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(TppConsentRestApi.BASE_PATH)
@RequiredArgsConstructor
public class TppConsentController implements TppConsentRestApi {
    private final TppConsentService tppConsentService;

    @Override
    public ResponseEntity<List<String>> generateConsents(List<AisConsent> consents) {
        return ResponseEntity.ok(tppConsentService.generateConsents(consents));
    }
}
