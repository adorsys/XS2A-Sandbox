package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.OpTypeTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;

public interface CmsAspspConsentDataService {
    <T> String toBase64String(T response);

    <T extends SCAResponseTO> GlobalScaResponseTO mapToGlobalResponse(T source, OpTypeTO type);

    int updateLoginFailedCount(String encryptedId);

    boolean isFailedLogin(String encryptedId);
}
