package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountAccess {
    private String id;
    private String iban;
    private Currency currency;
    private AccessTypeTO accessType;
    private int scaWeight;
    private String accountId;
}
