package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator.generateIbanForNispAccount;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.PredicateUtils.notNullPredicate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayload {
    private static final String SEPARATOR = "_";

    private List<UserTO> users;
    private List<AccountDetailsTO> accounts;
    private List<AccountBalance> balancesList;
    @JsonIgnore
    private Map<String, String> generatedIbans = new HashMap<>();

    public boolean isNotValidPayload() {
        return CollectionUtils.filter(users, notNullPredicate()) ||
                   CollectionUtils.filter(accounts, notNullPredicate()) ||
                   CollectionUtils.filter(balancesList, notNullPredicate());
    }

    public DataPayload updateIbanForBranch(String branch) {
        emptyIfNull(accounts).forEach(a -> a.setIban(generateIban(a.getIban(), branch)));
        emptyIfNull(balancesList).forEach(b -> b.setIban(generateIban(b.getIban(), branch)));
        emptyIfNull(users).forEach(u -> updateUserIbans(u, branch));

        return this;
    }

    private void updateUserIbans(UserTO user, String branch) {
        user.setId(buildValue(branch, user.getId()));
        user.setEmail(buildValue(branch, user.getEmail()));
        user.setLogin(buildValue(branch, user.getLogin()));
        user.getScaUserData()
            .forEach(d -> d.setMethodValue(buildValue(branch, d.getMethodValue())));
        user.getAccountAccesses()
            .forEach(a -> a.setIban(generateIban(a.getIban(), branch)));
    }

    private String generateIban(String iban, String branch) {
        if (generatedIbans.containsKey(iban)) {
            return generatedIbans.get(iban);
        }
        String generatedIban = generateIbanForNispAccount(branch, iban);
        generatedIbans.put(iban, generatedIban);
        return generatedIban;
    }

    private String buildValue(String branch, String suffix) {
        return branch + SEPARATOR + suffix;
    }
}
