package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReportTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static de.adorsys.ledgers.middleware.api.domain.account.BalanceTypeTO.INTERIM_AVAILABLE;
import static de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccessType.OWNER;
import static org.assertj.core.api.Assertions.assertThat;

public class AccountMapperTest {
    private static final String IBAN = "DE1234567890";
    private static final AccessTypeTO ACCESS_TYPE = AccessTypeTO.OWNER;
    private static final int SCA_WEIGHT = 20;
    private static final Currency CURRENCY = Currency.getInstance("EUR");

    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    public void toAccountDetailsTOTest() {
        DepositAccount input = getTppUiDepositAccount();
        AccountDetailsTO expectedResult = getAccountDetailsTO();

        AccountDetailsTO result = accountMapper.toAccountDetailsTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expectedResult);
    }

    @Test
    public void toAccountAccessTOTest() {
        AccountAccess input = getTppUiAccountAccess();
        AccountAccessTO expectedResult = getAccountAccessTO();

        AccountAccessTO result = accountMapper.toAccountAccessTO(input);
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expectedResult);
    }

    @Test
    public void toAccountReport() {
        AccountReport expected = new AccountReport(getDetails(), Arrays.asList(new UserAccess("LOGIN1", SCA_WEIGHT, OWNER), new UserAccess("LOGIN2", SCA_WEIGHT, OWNER)));
        AccountReport result = accountMapper.toAccountReport(getReportTO());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expected);
    }

    private AccountReportTO getReportTO() {
        return new AccountReportTO(getDetails(), Arrays.asList(getUser("LOGIN1"), getUser("LOGIN2")));
    }

    private UserTO getUser(String login) {
        UserTO user = new UserTO();
        user.setLogin(login);
        user.setAccountAccesses(Collections.singletonList(new AccountAccessTO("id", IBAN, CURRENCY, ACCESS_TYPE, SCA_WEIGHT)));
        return user;
    }

    @NotNull
    private AccountDetailsTO getDetails() {
        AccountDetailsTO details = new AccountDetailsTO();
        details.setIban(IBAN);
        details.setAccountStatus(ENABLED);
        details.setAccountType(CASH);
        details.setBalances(Collections.singletonList(new AccountBalanceTO(new AmountTO(CURRENCY, BigDecimal.TEN), INTERIM_AVAILABLE, null, null, null, null)));
        details.setCurrency(CURRENCY);
        return details;
    }

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, CASH, ENABLED, null, null, UsageTypeTO.PRIV, null, null);
    }

    private DepositAccount getTppUiDepositAccount() {
        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setId("XYZ");
        depositAccount.setAccountStatus(AccountStatus.ENABLED);
        depositAccount.setAccountType(AccountType.CASH);
        depositAccount.setCurrency(CURRENCY);
        depositAccount.setIban(IBAN);
        depositAccount.setUsageType(AccountUsage.PRIV);
        return depositAccount;
    }

    private AccountAccessTO getAccountAccessTO() {
        return new AccountAccessTO(null, IBAN, CURRENCY, ACCESS_TYPE, SCA_WEIGHT);
    }

    private AccountAccess getTppUiAccountAccess() {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setId("XyZ");
        accountAccess.setAccessType(ACCESS_TYPE);
        accountAccess.setIban(IBAN);
        accountAccess.setCurrency(CURRENCY);
        accountAccess.setScaWeight(SCA_WEIGHT);
        return accountAccess;
    }

}
