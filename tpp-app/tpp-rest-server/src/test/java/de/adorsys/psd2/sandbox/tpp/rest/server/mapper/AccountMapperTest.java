package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.Currency;

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

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO(null, IBAN, null, null, null, null, CURRENCY, null, null, AccountTypeTO.CASH, AccountStatusTO.ENABLED, null, null, UsageTypeTO.PRIV, null, null);
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
        return new AccountAccessTO(null, IBAN, ACCESS_TYPE, SCA_WEIGHT);
    }

    private AccountAccess getTppUiAccountAccess() {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setId("XyZ");
        accountAccess.setAccessType(ACCESS_TYPE);
        accountAccess.setIban(IBAN);
        accountAccess.setScaWeight(SCA_WEIGHT);
        return accountAccess;
    }

}
