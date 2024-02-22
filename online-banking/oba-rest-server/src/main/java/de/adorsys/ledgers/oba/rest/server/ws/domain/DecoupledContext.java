/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

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
