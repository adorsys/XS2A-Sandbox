package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.SinglePaymentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadedData {
    private List<UserTO> users = new ArrayList<>();
    private Map<String, AccountDetailsTO> details = new HashMap<>(); // k -> IBAN, v -> Details
    private Map<String, AccountBalance> balances = new HashMap<>();  // k -> IBAN, v -> Balance
    private List<SinglePaymentTO> payments = new ArrayList<>();
    private boolean generatePayments;
    private String branch;
}
