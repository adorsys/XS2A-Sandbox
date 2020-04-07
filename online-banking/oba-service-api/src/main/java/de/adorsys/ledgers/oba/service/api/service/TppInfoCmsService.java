package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;

import java.util.List;

public interface TppInfoCmsService {
    List<TppInfoTO> getTpps();
}
