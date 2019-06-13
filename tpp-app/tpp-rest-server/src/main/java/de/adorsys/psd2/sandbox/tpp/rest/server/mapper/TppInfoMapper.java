package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.TppInfo;
import org.springframework.stereotype.Component;

// TODO Use map struct instead https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/180
@Component
public class TppInfoMapper {

    public UserTO fromTppInfo(TppInfo info) {
        return new UserTO(info.getLogin(), info.getEmail(), info.getPin());
    }
}
