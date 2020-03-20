package de.adorsys.ledgers.oba.service.impl.service;

import de.adorsys.ledgers.oba.service.api.domain.TppInfoTO;
import de.adorsys.ledgers.oba.service.api.service.TppInfoCmsService;
import de.adorsys.ledgers.oba.service.impl.mapper.TppInfoObaMapper;
import de.adorsys.psd2.consent.domain.TppInfoEntity;
import de.adorsys.psd2.consent.repository.TppInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TppInfoCmsServiceImpl implements TppInfoCmsService {
    private final TppInfoRepository tppInfoRepository;
    private final TppInfoObaMapper tppInfoObaMapper;

    @Override
    @Transactional
    public List<TppInfoTO> getTpps() {
        List<TppInfoEntity> tpps = IterableUtils.toList(tppInfoRepository.findAll());
        return tppInfoObaMapper.toTppInfoTOs(tpps);
    }
}
