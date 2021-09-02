package de.adorsys.psd2.sandbox.cms.connector.api.domain;

import lombok.Data;

import java.util.List;

@Data
public class AccountAccessInfo {
    private List<AccountReferenceInfo> accounts;
    private List<AccountReferenceInfo> balances;
    private List<AccountReferenceInfo> transactions;
}
