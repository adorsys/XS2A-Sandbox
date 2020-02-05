package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.iban4j.CountryCode;
import org.iban4j.Iban;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TppData {
    private CountryCode countryCode;
    private String branchId;
    private long nextAccountNumber;

    /**
     * Empty list for Account Access is common situation for new Tpp user
     *
     * @param user Tpp user object
     */
    public TppData(UserTO user) {
        if (user == null || StringUtils.isEmpty(user.getBranch()) || user.getAccountAccesses() == null) {
            throw new TppException("Could not retrieve tpp data", 400);
        }
        String[] countryCodeAndBranchId = user.getBranch().split("_");
        this.countryCode = CountryCode.valueOf(countryCodeAndBranchId[0]);
        this.branchId = countryCodeAndBranchId[1];
        this.nextAccountNumber = user.getAccountAccesses().stream()
                                     .map(AccountAccessTO::getIban)
                                     .map(Iban::valueOf)
                                     .map(Iban::getAccountNumber)
                                     .map(Long::parseLong)
                                     .max(Comparator.comparingLong(Long::longValue))
                                     .map(i -> ++i)
                                     .orElse(100L);
    }

    public static Map<CountryCode, String> sortMapByValue(Map<CountryCode, String> map) {
        return map.entrySet()
                   .stream()
                   .sorted((Map.Entry.comparingByValue())) //NOPMD //each time different pmd violation
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }
}
