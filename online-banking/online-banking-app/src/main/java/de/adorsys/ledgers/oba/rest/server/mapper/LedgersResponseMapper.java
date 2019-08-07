package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.oba.rest.api.domain.AuthorizeResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LedgersResponseMapper {
    AuthorizeResponse toAuthorizeResponse(SCALoginResponseTO response);
}
