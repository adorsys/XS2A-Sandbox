package de.adorsys.ledgers.oba.rest.server.mapper;

import org.adorsys.ledgers.consent.aspsp.rest.client.CreatePiisConsentRequest;
import org.mapstruct.Mapper;

import de.adorsys.ledgers.oba.rest.api.domain.CreatePiisConsentRequestTO;

@Mapper(componentModel = "spring", uses = {CurrencyMapper.class, TimeMapper.class})
public interface CreatePiisConsentRequestMapper {
	CreatePiisConsentRequest fromCreatePiisConsentRequest(CreatePiisConsentRequestTO consentRequest);
}
