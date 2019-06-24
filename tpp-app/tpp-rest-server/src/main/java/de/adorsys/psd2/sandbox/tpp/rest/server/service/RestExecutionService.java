package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.UploadedData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestExecutionService {
    private final AccountMgmtStaffRestClient accountRestClient;
    private final UserMgmtStaffRestClient userRestClient;

    public void updateLedgers(DataPayload payload) {
        if (payload.isNotValidPayload()) {
            throw new TppException("Payload data is invalid", 400);
        }
        UploadedData data = initialiseDataSets(payload);
        updateUsers(data);
        updateBalances(data);
    }

    private UploadedData initialiseDataSets(DataPayload payload) {
        List<UserTO> users = Optional.ofNullable(payload.getUsers())
                                 .orElse(Collections.emptyList());
        Map<String, AccountDetailsTO> accounts = getTargetData(payload.getAccounts(), AccountDetailsTO::getIban);
        Map<String, AccountBalance> balances = getTargetData(payload.getBalancesList(), AccountBalance::getIban);

        return new UploadedData(users, accounts, balances);
    }

    private <T> Map<String, T> getTargetData(List<T> source, Function<T, String> function) {
        if (CollectionUtils.isEmpty(source)) {
            return new HashMap<>();
        }
        return source.stream()
                   .collect(Collectors.toMap(function, Function.identity()));
    }

    private void updateUsers(UploadedData data) {
        for (UserTO user : data.getUsers()) {
            user = userRestClient.createUser(user).getBody();

            Optional.ofNullable(user)
                .ifPresent(u -> {
                    if (!data.getDetails().isEmpty()) {
                        createAccountsForUser(u.getId(), u.getAccountAccesses(), data.getDetails());
                    }
                });
        }
    }

    private void createAccountsForUser(String userId, List<AccountAccessTO> accesses, Map<String, AccountDetailsTO> details) {
        accesses.stream()
            .filter(access -> details.containsKey(access.getIban()))
            .map(a -> details.get(a.getIban()))
            .forEach(account -> createAccount(userId, account));
    }

    private void createAccount(String userId, AccountDetailsTO account) {
        try {
            accountRestClient.createDepositAccountForUser(userId, account);
        } catch (FeignException f) {
            log.error("Account: {} {} creation error, probably it already exists", account.getIban(), account.getCurrency());
        }
    }

    private void updateBalances(UploadedData data) {
        if (data.getBalances().isEmpty()) {
            return;
        }
        List<AccountDetailsTO> accountsAtLedgers = Optional.ofNullable(accountRestClient.getListOfAccounts().getBody())
                                                       .orElse(Collections.emptyList());
        accountsAtLedgers
            .forEach(a -> updateBalanceIfPresent(a, data.getBalances()));
    }

    private void updateBalanceIfPresent(AccountDetailsTO detail, Map<String, AccountBalance> balances) {
        try {
            Optional.ofNullable(accountRestClient.getAccountDetailsById(detail.getId()).getBody())
                .ifPresent(d -> calculateDifAndUpdate(d, Optional.ofNullable(balances.get(d.getIban()))
                                                             .orElse(getZeroBalance(d))));
        } catch (FeignException f) {
            log.error("Could not retrieve balances for account: {}", detail.getIban());
        }
    }

    private void calculateDifAndUpdate(AccountDetailsTO detail, AccountBalance balance) {
        BigDecimal amountAtLedgers = detail.getBalances().get(0).getAmount().getAmount();
        BigDecimal requestedAmount = balance.getAmount();

        BigDecimal delta = requestedAmount.subtract(amountAtLedgers);
        if (delta.compareTo(BigDecimal.ZERO) > 0) {
            AmountTO amount = new AmountTO();
            amount.setCurrency(detail.getCurrency());
            amount.setAmount(delta);
            try {
                accountRestClient.depositCash(detail.getId(), amount);
            } catch (FeignException f) {
                log.error("Could not update balances for: {} with amount: {}", detail.getIban(), amount);
            }
        }
    }

    private AccountBalance getZeroBalance(AccountDetailsTO details) {
        return new AccountBalance(null, details.getIban(), details.getCurrency(), BigDecimal.ZERO);
    }
}
