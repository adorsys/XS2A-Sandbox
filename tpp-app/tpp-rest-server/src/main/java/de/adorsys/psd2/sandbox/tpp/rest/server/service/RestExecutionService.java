package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AccountMgmtStaffRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.UploadedData;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestExecutionService {
    private final AccountMgmtStaffRestClient accountRestClient;
    private final UserMgmtStaffRestClient userRestClient;

    public boolean updateLedgers(DataPayload payload) {
        boolean result = doUpdate(payload);
        log.info("Result of update is: {}", result ? "success" : "failure");
        return result;
    }

    private boolean doUpdate(DataPayload payload) {
        UploadedData data = initialiseDataSets(payload);
        return updateUsers(data) && updateBalances(data);
    }

    private UploadedData initialiseDataSets(DataPayload payload) {
        List<UserTO> users = Optional.ofNullable(payload.getUsers())
                                 .orElse(Collections.emptyList());
        Map<String, AccountDetailsTO> accounts = getAccountsForUploadedData(payload);
        Map<String, AccountBalance> balances = getBalancesForUploadedData(payload);

        return new UploadedData(users, accounts, balances);
    }

    private Map<String, AccountBalance> getBalancesForUploadedData(DataPayload payload) {
        return Optional.ofNullable(payload.getBalancesList())
                   .orElse(Collections.emptyList())
                   .stream()
                   .collect(Collectors.toMap(AccountBalance::getIban, b -> b));
    }

    private Map<String, AccountDetailsTO> getAccountsForUploadedData(DataPayload payload) {
        return Optional.ofNullable(payload.getAccounts())
                   .orElse(Collections.emptyList())
                   .stream()
                   .collect(Collectors.toMap(AccountDetailsTO::getIban, a -> a));
    }

    private boolean updateUsers(UploadedData data) {
        for (UserTO user : data.getUsers()) {
            try {
                user = userRestClient.createUser(user).getBody();
            } catch (FeignException f) {
                String msg = String.format("User: %s probably already exists", user.getLogin());
                if (f.status() == 500 || f.status() == 403) {
                    msg = String.format("Connection problem %s", f.getMessage());
                    log.error(msg);
                    return false;
                }
                log.error(msg);
            }
            Optional.ofNullable(user)
                .ifPresent(u -> {
                    if (!data.getDetails().isEmpty()) {
                        createAccountsForUser(u.getId(), u.getAccountAccesses(), data.getDetails());
                    }
                });
        }
        return true;
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

    private boolean updateBalances(UploadedData data) {
        if (data.getBalances().isEmpty()) {
            return true;
        }
        try {
            List<AccountDetailsTO> accountsAtLedgers = Optional.ofNullable(accountRestClient.getListOfAccounts().getBody())
                                                           .orElse(Collections.emptyList());
            accountsAtLedgers
                .forEach(a -> updateBalanceIfPresent(a, data.getBalances()));
            return true;
        } catch (FeignException e) {
            log.error("Could not retrieve accounts from Ledgers");
            return false;
        }
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
