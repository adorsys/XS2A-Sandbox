package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.resource.TppRestApi;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import de.adorsys.psd2.sandbox.tpp.rest.server.service.IbanGenerationService;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping(TppRestApi.BASE_PATH)
public class TppController implements TppRestApi {
    private final UserMapper userMapper;
    private final UserMgmtStaffRestClient userMgmtStaffRestClient;
    private final UserMgmtRestClient userMgmtRestClient;
    private final DataRestClient dataRestClient;
    private final IbanGenerationService ibanGenerationService;

    @Override
    public void login(String login, String pin) {
    }

    @Override
    public ResponseEntity<List<CountryCode>> getCountryCodes() {
        return ResponseEntity.ok(ibanGenerationService.getSupportedCountryCodes());
    }

    @Override
    public ResponseEntity<BankCodeStructure> getBankCodeStructure(String countryCode) {
        return ResponseEntity.ok(ibanGenerationService.getBankCodeStructure(CountryCode.valueOf(countryCode)));

    }

    @Override
    public ResponseEntity<Void> register(User user) {
        userMgmtStaffRestClient.register(user.getId(), userMapper.toUserTO(user));
        return ResponseEntity.status(CREATED).build();
    }

    @Override
    public ResponseEntity<Void> remove() {
        String branchId = userMgmtRestClient.getUser().getBody().getBranch();
        return dataRestClient.branch(branchId);
    }

    @Override
    public ResponseEntity<Void> transactions(String iban) {
        return dataRestClient.account(iban);
    }
}
