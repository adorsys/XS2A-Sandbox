package de.adorsys.psd2.sandbox.admin.rest.api.domain;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountReport {
    private AccountDetailsTO details;
    private List<UserAccess> accesses;
    private boolean multilevelScaEnabled;
}
