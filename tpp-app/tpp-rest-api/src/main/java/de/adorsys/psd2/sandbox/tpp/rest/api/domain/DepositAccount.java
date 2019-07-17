package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import lombok.Data;

import java.util.Currency;

@Data
public class DepositAccount {
    private String id;
    private AccountType accountType;
    private AccountUsage usageType;
    private Currency currency;
    private String iban;
    private AccountStatus accountStatus;
}
