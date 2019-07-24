package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.junit.Assert;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Currency;

public class AccountMapperTest {
    private final AccountMapper accountMapper = Mappers.getMapper(AccountMapper.class);

    @Test
    public void toAccountDetailsTOTest() {
        DepositAccount depositAccount = new DepositAccount();
        depositAccount.setAccountStatus(AccountStatus.ENABLED);
        depositAccount.setAccountType(AccountType.CASH);
        depositAccount.setCurrency(Currency.getInstance("EUR"));
        depositAccount.setIban("DE1234567890");
        depositAccount.setUsageType(AccountUsage.PRIV);

        AccountDetailsTO accountDetails = accountMapper.toAccountDetailsTO(depositAccount);

        Assert.assertEquals(accountDetails.getAccountStatus().toString(), depositAccount.getAccountStatus().toString());
        Assert.assertEquals(accountDetails.getAccountType().toString(), depositAccount.getAccountType().toString());
        Assert.assertEquals(accountDetails.getCurrency(), depositAccount.getCurrency());
        Assert.assertEquals(accountDetails.getIban(), depositAccount.getIban());
        Assert.assertEquals(accountDetails.getUsageType().toString(), depositAccount.getUsageType().toString());

    }

    @Test
    public void toAccountAccessTOTest() {
        AccountAccess accountAccess = new AccountAccess();
        accountAccess.setAccessType(AccessTypeTO.OWNER);
        accountAccess.setIban("DE1234567890");
        accountAccess.setScaWeight(20);

        AccountAccessTO accountAccessTO = accountMapper.toAccountAccessTO(accountAccess);

        Assert.assertEquals(accountAccessTO.getIban(), accountAccess.getIban());
        Assert.assertEquals(accountAccessTO.getAccessType().toString(), accountAccess.getAccessType().toString());
        Assert.assertEquals(accountAccessTO.getScaWeight(), accountAccess.getScaWeight());

    }

}
