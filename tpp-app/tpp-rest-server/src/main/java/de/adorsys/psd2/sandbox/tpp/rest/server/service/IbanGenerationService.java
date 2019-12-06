package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.BankCodeStructure;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.TppData;
import lombok.RequiredArgsConstructor;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.iban4j.bban.BbanStructure;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

import static org.iban4j.CountryCode.*;
import static org.iban4j.bban.BbanEntryType.account_number;

@Service
@RequiredArgsConstructor
public class IbanGenerationService {
    private static final List<CountryCode> COUNTRY_CODES = Arrays.asList(DE, CH, FR, UA, DK, FI, HU, IS, LI, LU, PL, PT, SE, SI, GB);

    private final UserMgmtRestClient userMgmtRestClient;

    public String generateNextIban() {
        TppData data = getTppData();
        return generateIban(data.getCountryCode(), data.getBranchId(), data.getNextAccountNumber());
    }

    public String generateIbanForNisp(DataPayload payload, String iban) {
        if (payload.getGeneratedIbans().containsKey(iban)) {
            return payload.getGeneratedIbans().get(iban);
        }
        TppData data = getTppData();
        String generatedIban = generateIban(data.getCountryCode(), data.getBranchId(), Long.parseLong(iban));
        payload.getGeneratedIbans().put(iban, generatedIban);
        return generatedIban;
    }

    public List<CountryCode> getSupportedCountryCodes() {
        return COUNTRY_CODES;
    }

    public BankCodeStructure getBankCodeStructure(CountryCode code) {
        return new BankCodeStructure(code);
    }

    private String generateIban(CountryCode countryCode, String bankCode, long accountNr) {
        int accountNumberLength = BbanStructure.forCountry(countryCode).getEntries().stream()
                                      .filter(e -> e.getEntryType().equals(account_number))
                                      .findFirst().get().getLength();
        String formatParam = "%0" + accountNumberLength + "d";
        String accountNumber = String.format(formatParam, accountNr);
        return new Iban.Builder()
                   .countryCode(countryCode)
                   .bankCode(bankCode)
                   .accountNumber(accountNumber)
                   .buildRandom()
                   .toString();
    }

    private TppData getTppData() {
        return new TppData(userMgmtRestClient.getUser().getBody());
    }
}
