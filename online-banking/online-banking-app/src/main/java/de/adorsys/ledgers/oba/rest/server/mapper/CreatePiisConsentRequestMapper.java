package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;
import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {TimeMapper.class})
public interface CreatePiisConsentRequestMapper {
    CreatePiisConsentRequest fromCreatePiisConsentRequest(CreatePiisConsentRequestTO consentRequest);
}
