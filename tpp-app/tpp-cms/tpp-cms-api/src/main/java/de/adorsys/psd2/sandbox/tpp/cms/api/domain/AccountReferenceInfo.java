package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountReferenceInfo {
    private String resourceId;
    private String aspspAccountId;
    private String accountIdentifier;
    private String currency;
    private UserAccountReferenceType accountType;
}
