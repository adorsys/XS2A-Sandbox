package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.sca.ChallengeDataTO;
import de.adorsys.ledgers.middleware.api.domain.sca.SCALoginResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.service.api.domain.AuthorizeResponse;
import de.adorsys.ledgers.oba.service.impl.mapper.LedgersResponseMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO.RECEIVED;
import static org.assertj.core.api.Assertions.assertThat;

public class LedgersResponseMapperTest {
    private static final String SCA_ID = "SCA QWERTY";
    private static final String AUTH_ID = "AUTH QWERTY";
    private static final LocalDateTime STATUS_DATE = LocalDateTime.of(1981, 7, 11, 1, 1);
    private static final String ACCESS_TOKEN = "TEST ACCESS TOKEN";
    LedgersResponseMapper mapper = Mappers.getMapper(LedgersResponseMapper.class);

    @Test
    public void toAuthorizeResponse() {
        AuthorizeResponse result = mapper.toAuthorizeResponse(getLoginResponse());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getAuthorizeResponse());
    }

    private AuthorizeResponse getAuthorizeResponse() {
        AuthorizeResponse response = new AuthorizeResponse();
        response.setAuthorisationId(AUTH_ID);
        response.setEncryptedConsentId(null);
        response.setScaMethods(getScaMethods());
        response.setScaStatus(RECEIVED);
        response.setPsuMessages(new ArrayList<>());
        return response;
    }

    private SCALoginResponseTO getLoginResponse() {
        SCALoginResponseTO response = new SCALoginResponseTO();
        response.setScaId(SCA_ID);
        response.setScaStatus(RECEIVED);
        response.setAuthorisationId(AUTH_ID);
        response.setScaMethods(getScaMethods());
        response.setChosenScaMethod(getScaUserData("1"));
        response.setPsuMessage("TEST MESSAGE");
        response.setStatusDate(STATUS_DATE);
        response.setExpiresInSeconds(600);
        response.setChallengeData(new ChallengeDataTO());
        response.setMultilevelScaRequired(false);
        response.setBearerToken(new BearerTokenTO(ACCESS_TOKEN, "Bearer", 600, null, new AccessTokenTO()));
        return response;
    }

    private List<ScaUserDataTO> getScaMethods() {
        return Arrays.asList(getScaUserData("1"), getScaUserData("2"));
    }

    private ScaUserDataTO getScaUserData(String id) {
        return new ScaUserDataTO(id, ScaMethodTypeTO.EMAIL, id + "@" + id + ".test", null, false, null, false, false);
    }
}
