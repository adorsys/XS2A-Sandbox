package de.adorsys.psd2.sandbox.admin.rest.server.controller;

import de.adorsys.psd2.sandbox.admin.rest.api.resource.AdminDataRestApi;
import de.adorsys.psd2.sandbox.admin.rest.server.service.IbanGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(AdminDataRestApi.BASE_PATH)
public class AdminDataController implements AdminDataRestApi {

    private final IbanGenerationService ibanGenerationService;

    @Override
    public ResponseEntity<String> generateIban(String tppId) {
        return ResponseEntity.ok(ibanGenerationService.generateNextIban(tppId));
    }

}
