package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositAccount {
    private String id;
    private AccountType accountType;
    private AccountUsage usageType;
    private Currency currency;
    private String iban;
    private AccountStatus accountStatus;
}
