package de.adorsys.ledgers.oba.rest.server.ws.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.security.Principal;

@Data
@AllArgsConstructor
public class StompPrincipal implements Principal {
    String login;

    @Override
    public String getName() {
        return login;
    }
}
