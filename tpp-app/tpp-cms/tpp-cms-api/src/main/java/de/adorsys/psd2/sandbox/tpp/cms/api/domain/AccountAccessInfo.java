package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import lombok.Data;

import java.util.List;

@Data
public class AccountAccessInfo {
    private List<UserAccountInfo> accounts;
    private List<UserAccountInfo> balances;
    private List<UserAccountInfo> transactions;
    private UserAccountAccessType availableAccounts;
    private UserAccountAccessType allPsd2;
    private UserAccountAccessType availableAccountsWithBalance;
}
