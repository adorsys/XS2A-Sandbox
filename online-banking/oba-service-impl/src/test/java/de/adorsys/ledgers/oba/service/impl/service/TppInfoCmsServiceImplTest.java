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
