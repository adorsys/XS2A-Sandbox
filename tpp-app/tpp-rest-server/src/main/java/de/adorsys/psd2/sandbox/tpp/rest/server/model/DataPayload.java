package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayload {
    private List<UserTO> users;
    private List<AccountDetailsTO> accounts;
    private List<AccountBalance> balancesList;
    private List<SinglePaymentTO> payments;

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

    private void fixNullValues() {
        this.users = emptyIfNull(users);
        this.accounts = emptyIfNull(accounts);
        this.balancesList = emptyIfNull(balancesList);
        this.payments = emptyIfNull(payments);
    }

    private <T> boolean notContainsNullElements(List<T> list) {
        return !list.contains(null);
    }
}
