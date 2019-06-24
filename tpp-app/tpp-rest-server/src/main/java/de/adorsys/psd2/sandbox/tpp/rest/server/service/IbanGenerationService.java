package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.utils.IbanGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class IbanGenerationService {
    private static final String MSG_NO_IBAN_AVAILABLE = "Could not generate new IBAN, seems you used out all possible combinations";
    private static final String MSG_USER_NOT_FOUND = "User Not Found";

    private final UserMgmtRestClient userMgmtRestClient;

    public String generateRandomIban() {
        UserTO user = userMgmtRestClient.getUser()
                          .getBody();
        List<AccountAccessTO> access = Optional.ofNullable(user)
                                           .map(UserTO::getAccountAccesses)
                                           .orElseThrow(() -> new TppException(MSG_USER_NOT_FOUND, 404));
        return getNextFreeIban(access, user.getBranch());
    }

    private String getNextFreeIban(List<AccountAccessTO> access, String branch) {
        return IntStream.range(0, 100)
                   .mapToObj(i -> IbanGenerator.generateRandomIban(branch, i))
                   .filter(iban -> isNotContainingIban(access, iban))
                   .findFirst()
                   .orElseThrow(() -> new TppException(MSG_NO_IBAN_AVAILABLE, 400));
    }

    private boolean isNotContainingIban(List<AccountAccessTO> access, String iban) {
        return access.stream()
                   .noneMatch(a -> a.getIban().equals(iban));
    }
}
