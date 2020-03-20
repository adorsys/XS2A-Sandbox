package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TppInfoObaMapper {

    TppInfoTO toTppInfoTO(TppInfoEntity source);

    List<TppInfoTO> toTppInfoTOs(List<TppInfoEntity> source);
}
