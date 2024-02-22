/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TppInfoObaMapperTest {

    private static final TppInfoObaMapper mapper = Mappers.getMapper(TppInfoObaMapper.class);

    @Test
    void toTppInfoTO() {
        // When
        TppInfoTO result = mapper.toTppInfoTO(getTppInfoEntity());

        // Then
        assertThat(result).isEqualToComparingFieldByFieldRecursively(getTppInfoTO());
    }

    @Test
    void toTppInfoTOs() {
        // When
        List<TppInfoTO> result = mapper.toTppInfoTOs(Collections.singletonList(getTppInfoEntity()));

        // Then
        assertThat(result).isEqualTo(Collections.singletonList(getTppInfoTO()));
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

}
