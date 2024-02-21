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

package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.oba.service.impl.mapper.TppInfoObaMapper;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.repository.TppInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TppInfoCmsServiceImplTest {

    @InjectMocks
    private TppInfoCmsServiceImpl tppInfoCmsService;

    @Mock
    private TppInfoRepository tppInfoRepository;
    @Mock
    private TppInfoObaMapper tppInfoObaMapper;

    @Test
    void getTpps() {
        // Given
        when(tppInfoRepository.findAll()).thenReturn(Collections.singletonList(new TppInfoEntity()));
        when(tppInfoObaMapper.toTppInfoTOs(any())).thenReturn(Collections.singletonList(new TppInfoTO()));

        // When
        List<TppInfoTO> result = tppInfoCmsService.getTpps();

        // Then
        assertFalse(result.isEmpty());
    }
}
