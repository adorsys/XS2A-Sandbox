package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayload {
    private List<UserTO> users;
    private List<AccountDetailsTO> accounts;
    private List<AccountBalance> balancesList;
    private List<PaymentTO> payments;
    private static final String SEPARATOR = "_";

    @JsonIgnore
    private boolean generatePayments;

    @JsonIgnore
    private String branch;

    @JsonIgnore
    private Map<String, String> generatedIbans = new HashMap<>();

    @JsonIgnore
    public boolean isValidPayload() {
        fixNullValues();
        return notContainsNullElements(users)
                   && notContainsNullElements(accounts)
                   && notContainsNullElements(balancesList)
                   && notContainsNullElements(payments);
    }

    @JsonIgnore
    public Map<String, AccountDetailsTO> getAccountByIban() {
        return getTargetData(accounts, AccountDetailsTO::getIban);
    }

    @JsonIgnore
    public Map<String, AccountBalance> getBalancesByIban() {
        return getTargetData(balancesList, AccountBalance::getIban);
    }

    @JsonIgnore
    public DataPayload updatePayload(BiFunction<DataPayload, String, String> ibanGenerator, String branch, Currency currency, boolean generatePayments) {
        this.branch = branch;
        this.generatePayments = generatePayments;
        this.accounts.forEach(a -> {
            a.setIban(ibanGenerator.apply(this, a.getIban()));
            a.setCurrency(currency);
        });
        this.balancesList.forEach(b -> {
            b.setIban(ibanGenerator.apply(this, b.getIban()));
            b.setCurrency(currency);
        });

        this.users.forEach(u -> updateUserAndAccesses(ibanGenerator, u, branch, currency));
        return this;
    }

    private void updateUserAndAccesses(BiFunction<DataPayload, String, String> function, UserTO user, String branch, Currency currency) {
        user.setId(buildValue(branch, user.getId()));
        user.setEmail(buildValue(branch, user.getEmail()));
        user.setLogin(buildValue(branch, user.getLogin()));
        user.getScaUserData().forEach(d -> d.setMethodValue(buildValue(branch, d.getMethodValue())));
        user.getAccountAccesses()
            .forEach(a -> {
                a.setIban(function.apply(this, a.getIban()));
                a.setCurrency(currency);
            });
    }

    private <T> Map<String, T> getTargetData(List<T> source, Function<T, String> function) {
        if (CollectionUtils.isEmpty(source)) {
            return new HashMap<>();
        }
        return source.stream()
                   .collect(Collectors.toMap(function, Function.identity()));
    }

    private void fixNullValues() {
        this.users = emptyIfNull(users);
        this.accounts = emptyIfNull(accounts);
        this.balancesList = emptyIfNull(balancesList);
        this.payments = emptyIfNull(payments);
    }

    private <T> boolean notContainsNullElements(List<T> list) {
        return !list.contains(null);
    }

    private String buildValue(String branch, String suffix) {
        return branch + SEPARATOR + suffix;
    }
}
