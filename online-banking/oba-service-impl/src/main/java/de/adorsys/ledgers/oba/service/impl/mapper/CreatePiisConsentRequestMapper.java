package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.oba.service.api.domain.CreatePiisConsentRequestTO;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface CreatePiisConsentRequestMapper {
    CreatePiisConsentRequest fromCreatePiisConsentRequest(CreatePiisConsentRequestTO consentRequest);
}
