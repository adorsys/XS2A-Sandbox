package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestsDataGenerationService {
    private final ParseService parseService;
    private final RestExecutionService executionService;
    private final UserMgmtRestClient userMgmtRestClient;

    public byte[] generate() {
        UserTO userTO = userMgmtRestClient.getUser()
                            .getBody();

        String branch = Optional.ofNullable(userTO)
                            .map(UserTO::getBranch)
                            .orElseThrow(() -> new RuntimeException("User not found"));

        DataPayload dataPayload = parseService.getDefaultData()
                                      .map(d -> generateData(d, branch))
                                      .orElseThrow(() -> new RuntimeException("Seems no data is present in file!"));

        executionService.updateLedgers(dataPayload);
        return parseService.getFile(dataPayload);
    }

    private DataPayload generateData(DataPayload data, String branch) {
        Map<String, AccountDetailsTO> detailsMap = getNotNullList(data.getAccounts()).stream()
                                                       .map(a -> generateDetails(a, branch))
                                                       .collect(Collectors.toMap(this::getLastTwoSymbols, a -> a));
        data.setAccounts(new ArrayList<>(detailsMap.values()));
        List<AccountBalance> balances = getNotNullList(data.getBalancesList()).stream()
                                            .map(b -> generateBalances(b, branch, detailsMap))
                                            .collect(Collectors.toList());
        data.setBalancesList(balances);
        List<UserTO> users = getNotNullList(data.getUsers()).stream()
                                 .map(u -> generateUsers(u, branch, detailsMap))
                                 .collect(Collectors.toList());
        data.setUsers(users);
        return data;
    }

    private <T> List<T> getNotNullList(List<T> list) {
        return Optional.ofNullable(list).orElse(Collections.emptyList());
    }

    @NotNull
    private String getLastTwoSymbols(AccountDetailsTO a) {
        return a.getIban()
                   .substring(a.getIban().length() - 2);
    }

    private AccountBalance generateBalances(AccountBalance balance, String branch, Map<String, AccountDetailsTO> detailsMap) {
        String iban = getGeneratedIbanOrNew(balance.getIban(), branch, detailsMap);
        balance.setIban(iban);
        return balance;
    }

    private AccountDetailsTO generateDetails(AccountDetailsTO details, String branch) {
        String iban = generateIban(branch, details.getIban());
        details.setIban(iban);
        return details;
    }

    private UserTO generateUsers(UserTO user, String branch, Map<String, AccountDetailsTO> detailsMap) {
        user.setId(addBranchPrefix(branch, user.getId()));
        user.setEmail(addBranchPrefix(branch, user.getEmail()));
        user.setLogin(addBranchPrefix(branch, user.getLogin()));
        user.getScaUserData()
            .forEach(d -> d.setMethodValue(addBranchPrefix(branch, d.getMethodValue())));
        user.getAccountAccesses()
            .forEach(a -> a.setIban(getGeneratedIbanOrNew(a.getIban(), branch, detailsMap)));
        return user;
    }

    @NotNull
    private String addBranchPrefix(String branch, String concatObj) {
        return branch + "_" + concatObj;
    }

    private String generateIban(String branch, String iban) {
        return IbanGenerator.generateIban(branch, iban);
    }

    private String getGeneratedIbanOrNew(String iban, String branch, Map<String, AccountDetailsTO> detailsMap) {
        return detailsMap.containsKey(iban)
                   ? detailsMap.get(iban).getIban()
                   : generateIban(branch, iban);
    }
}
