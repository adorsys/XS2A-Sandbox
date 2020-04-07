package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.oba.service.api.service.TppInfoCmsService;
import de.adorsys.ledgers.oba.service.impl.mapper.TppInfoObaMapper;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.repository.TppInfoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TppInfoCmsServiceImplTest {

    @InjectMocks
    private TppInfoCmsServiceImpl tppInfoCmsService;

    @Mock
    private TppInfoRepository tppInfoRepository;
    @Mock
    private TppInfoObaMapper tppInfoObaMapper;

    @Test
    public void getTpps() {
        when(tppInfoRepository.findAll()).thenReturn(Collections.singletonList(new TppInfoEntity()));
        when(tppInfoObaMapper.toTppInfoTOs(any())).thenReturn(Collections.singletonList(new TppInfoTO()));
        List<TppInfoTO> result = tppInfoCmsService.getTpps();
        assertThat(result).isNotEmpty();
    }
}
