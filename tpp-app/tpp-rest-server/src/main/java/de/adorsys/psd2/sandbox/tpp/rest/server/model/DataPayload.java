package de.adorsys.psd2.sandbox.tpp.rest.server.model;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataPayload {

    @NotNull
    private List<UserTO> users;

    @NotNull
    private List<AccountDetailsTO> accounts;

    @NotNull
    private List<AccountBalance> balancesList;

}
