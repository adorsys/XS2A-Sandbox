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

    private final ParseService parseService;
    private final RestExecutionService executionService;
    private final UserMgmtRestClient userMgmtRestClient;

    public byte[] generate() {
        Optional<UserTO> user = Optional.ofNullable(userMgmtRestClient.getUser()
                                                        .getBody());

        String branch = user.map(UserTO::getBranch)
                            .orElseThrow(() -> new TppException(MSG_NO_BRANCH_SET, 400));

        DataPayload payload = parseService.getDefaultData()
                                  .map(p -> p.updateIbanForBranch(branch))
                                  .orElseThrow(() -> new TppException(CAN_NOT_LOAD_DEFAULT_DATA, 400));

        executionService.updateLedgers(payload);
        return parseService.generateFileByPayload(payload);
    }
}
