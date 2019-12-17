package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LedgersResponseMapper {
    AuthorizeResponse toAuthorizeResponse(SCALoginResponseTO response);
}
