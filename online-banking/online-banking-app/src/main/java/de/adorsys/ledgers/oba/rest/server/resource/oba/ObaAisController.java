package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.TransactionTO;
import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaAisApi;
import de.adorsys.ledgers.oba.rest.server.service.AisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(ObaAisController.BASE_PATH)
@RequiredArgsConstructor
public class ObaAisController implements ObaAisApi {
    private final AisService aisService;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<AccountDetailsTO>> accounts(String userLogin) {
        return ResponseEntity.ok(aisService.getAccounts(userLogin));
    }

    @Override
    public ResponseEntity<List<TransactionTO>> transactions(String accountId, LocalDate dateFrom, LocalDate dateTo) {
        return ResponseEntity.ok(aisService.getTransactions(accountId, dateFrom, dateTo));
    }
}
