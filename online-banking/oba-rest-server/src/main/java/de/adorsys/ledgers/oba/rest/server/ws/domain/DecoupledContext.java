package de.adorsys.ledgers.oba.rest.server.ws.domain;

import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
@RequiredArgsConstructor
public class DecoupledContext {
    private final Map<String, List<DecoupledConfRequest>> undeliveredDecoupledMessages = new HashMap<>();
    private final SimpUserRegistry simpUserRegistry;

    public List<DecoupledConfRequest> getUndeliveredMessages(String userLogin) {
        return Optional.ofNullable(undeliveredDecoupledMessages.get(userLogin))
                   .orElseGet(ArrayList::new);
    }

    public void addUndeliveredMessage(String login, DecoupledConfRequest message) {
        List<DecoupledConfRequest> unsentMessages = getUndeliveredMessages(login);
        unsentMessages.add(message);
        undeliveredDecoupledMessages.put(login, unsentMessages);
    }

    public void clearUndeliveredMessages(String login) {
        undeliveredDecoupledMessages.remove(login);
    }

    public boolean checkUserIsConnected(String login) {
        return simpUserRegistry.getUsers().stream()
                   .map(SimpUser::getName)
                   .anyMatch(login::equals);

    }
}
