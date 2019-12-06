package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestsDataGenerationService {
    private static final String MSG_NO_BRANCH_SET = "This User does not belong to any Branch";
    private static final String CAN_NOT_LOAD_DEFAULT_DATA = "Can't load default data";
    private static final String SEPARATOR = "_";

    private final ParseService parseService;
    private final RestExecutionService executionService;
    private final UserMgmtRestClient userMgmtRestClient;
    private final IbanGenerationService ibanGenerationService;

    public byte[] generate(boolean generatePayments) {
        Optional<UserTO> user = Optional.ofNullable(userMgmtRestClient.getUser()
                                                        .getBody());

        String branch = user.map(UserTO::getBranch)
                            .orElseThrow(() -> new TppException(MSG_NO_BRANCH_SET, 400));

        DataPayload payload = parseService.getDefaultData()
                                  .map(p -> updateIbanForBranch(p, branch))
                                  .orElseThrow(() -> new TppException(CAN_NOT_LOAD_DEFAULT_DATA, 400));
        payload.setBranch(branch);
        payload.setGeneratePayments(generatePayments);

        executionService.updateLedgers(payload);
        return parseService.generateFileByPayload(payload);
    }

    private DataPayload updateIbanForBranch(DataPayload dataPayload, String branch) {
        dataPayload.getAccounts().forEach(a -> a.setIban(ibanGenerationService.generateIbanForNisp(dataPayload, a.getIban())));
        dataPayload.getBalancesList().forEach(b -> b.setIban(ibanGenerationService.generateIbanForNisp(dataPayload, b.getIban())));
        dataPayload.getUsers().forEach(u -> updateUserIbans(dataPayload, u, branch));

        return dataPayload;
    }

    private void updateUserIbans(DataPayload dataPayload, UserTO user, String branch) {
        user.setId(buildValue(branch, user.getId()));
        user.setEmail(buildValue(branch, user.getEmail()));
        user.setLogin(buildValue(branch, user.getLogin()));
        user.getScaUserData()
            .forEach(d -> d.setMethodValue(buildValue(branch, d.getMethodValue())));
        user.getAccountAccesses()
            .forEach(a -> a.setIban(ibanGenerationService.generateIbanForNisp(dataPayload, a.getIban())));
    }

    private String buildValue(String branch, String suffix) {
        return branch + SEPARATOR + suffix;
    }

}
