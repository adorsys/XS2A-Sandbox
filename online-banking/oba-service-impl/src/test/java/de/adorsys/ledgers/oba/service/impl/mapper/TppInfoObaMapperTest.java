package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TppInfoObaMapperTest {

    private static final TppInfoObaMapper mapper = Mappers.getMapper(TppInfoObaMapper.class);

    @Test
    public void toTppInfoTO() {
        TppInfoTO result = mapper.toTppInfoTO(getTppInfoEntity());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getTppInfoTO());
    }

    private TppInfoTO getTppInfoTO() {
        TppInfoTO info = new TppInfoTO();
        info.setId(1L);
        info.setAuthorisationNumber("authNr");
        info.setTppName("name");
        info.setTppRoles(Collections.singletonList(TppRole.ASPSP));
        info.setAuthorityId("authorityId");
        info.setAuthorityName("authorityName");
        info.setCountry("DE");
        info.setOrganisation("organisation");
        info.setOrganisationUnit("orgUnit");
        info.setCity("Nurnberg");
        info.setState("Bavaria");
        return info;
    }

    private TppInfoEntity getTppInfoEntity() {
        TppInfoEntity info = new TppInfoEntity();
        info.setId(1L);
        info.setAuthorisationNumber("authNr");
        info.setTppName("name");
        info.setTppRoles(Collections.singletonList(TppRole.ASPSP));
        info.setAuthorityId("authorityId");
        info.setAuthorityName("authorityName");
        info.setCountry("DE");
        info.setOrganisation("organisation");
        info.setOrganisationUnit("orgUnit");
        info.setCity("Nurnberg");
        info.setState("Bavaria");
        return info;
    }

    @Test
    public void toTppInfoTOs() {
        List<TppInfoTO> result = mapper.toTppInfoTOs(Collections.singletonList(getTppInfoEntity()));
        assertThat(result).isEqualTo(Collections.singletonList(getTppInfoTO()));
    }
}
