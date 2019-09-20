package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;

public class BalanceMapperTest {
    private static final String ACCOUNT_ID = "ACCOUNT ID";
    private static final String IBAN = "DE12345678";
    private static final Currency EUR = Currency.getInstance("EUR");
    private static final BigDecimal AMOUNT = BigDecimal.TEN;
    private static final BalanceMapper mapper = Mappers.getMapper(BalanceMapper.class);

    @Test
    public void toAccountBalanceTO() {
        AccountBalanceTO result = mapper.toAccountBalanceTO(getBalance());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected());
    }

    private AccountBalanceTO expected() {
        return new AccountBalanceTO(new AmountTO(EUR, AMOUNT), null, null, null, null, IBAN);
    }

    private AccountBalance getBalance() {
        return new AccountBalance(ACCOUNT_ID, IBAN, EUR, AMOUNT);
    }
}
