package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.DecoupledConfRequest;

public interface DecoupledService {
    boolean executeDecoupledOpr(DecoupledConfRequest request, String token);
}
