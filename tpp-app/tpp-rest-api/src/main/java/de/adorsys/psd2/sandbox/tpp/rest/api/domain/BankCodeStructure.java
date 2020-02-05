package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.iban4j.CountryCode;
import org.iban4j.bban.BbanStructure;
import org.iban4j.bban.BbanStructureEntry;

import static org.iban4j.bban.BbanEntryType.bank_code;

@Getter
public class BankCodeStructure {
    @JsonIgnore
    private final CountryCode countryCode;
    private int length;
    private BbanStructureEntry.EntryCharacterType type;

    public BankCodeStructure(CountryCode countryCode) {
        this.countryCode = countryCode;
        this.length = BbanStructure.forCountry(countryCode).getEntries().stream()
                          .filter(e -> e.getEntryType().equals(bank_code))
                          .findFirst()
                          .map(BbanStructureEntry::getLength)
                          .orElse(0);
        this.type = BbanStructure.forCountry(countryCode).getEntries()
                        .stream()
                        .filter(e -> e.getEntryType() == bank_code)
                        .findFirst()
                        .map(BbanStructureEntry::getCharacterType).orElseThrow(() -> new IllegalArgumentException("Can't define character type"));
    }

    @JsonIgnore
    public boolean isCharacterType() {
        return BbanStructureEntry.EntryCharacterType.n == getType();
    }

    @JsonIgnore
    public boolean isNotCharacterType() {
        return !isCharacterType();
    }
}
