package de.adorsys.psd2.sandbox.admin.rest.server.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Currency;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalance {

    private String accountId;

    @NotNull
    private String iban;

    @NotNull
    private Currency currency;

    @NotNull
    private BigDecimal amount;

}
