package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserTO ttpInfoToUserTO(TppInfo tppInfo);

    UserTO toUserTO(User user);

}
