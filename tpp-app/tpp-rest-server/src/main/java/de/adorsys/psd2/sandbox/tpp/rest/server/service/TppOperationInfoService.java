package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationInfoEntity;
import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationTypeEntity;
import de.adorsys.psd2.sandbox.tpp.db.api.repository.OperationInfoRepository;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationType;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.OperationInfoMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TppOperationInfoService {
    private final OperationInfoRepository infoRepository;
    private final UserMgmtRestClient userMgmtRestClient;
    private final OperationInfoMapper infoMapper;

    public List<OperationInfo> getOperationsByTypeAndTppId(OperationType operationType) {
        String tppId = validateAndGetTppId();
        List<OperationInfoEntity> infos = Optional.ofNullable(operationType)
                                              .map(o -> infoRepository.findAllByTppIdAndOperationTypeOrderByCreatedDesc(tppId, OperationTypeEntity.valueOf(o.name())))
                                              .orElseGet(() -> infoRepository.findAllByTppIdOrderByCreatedDesc(tppId));
        return infoMapper.toOperationInfos(infos);
    }

    public OperationInfo createInfo(OperationInfo operationInfo) {
        String tppId = validateAndGetTppId();
        operationInfo.setTppId(tppId);
        OperationInfoEntity infoEntity = infoMapper.toOperationInfoEntity(operationInfo);
        return infoMapper.toOperationInfo(infoRepository.save(infoEntity));
    }

    public void deleteInfo(Long operationInfoId) {
        String tppId = validateAndGetTppId();
        if (!infoRepository.existsByIdAndTppId(operationInfoId, tppId)) {
            throw new TppException("Requested data doesnt belong to Your TPP", 403);
        }
        infoRepository.deleteById(operationInfoId);
    }

    private String validateAndGetTppId() {
        UserTO user = userMgmtRestClient.getUser().getBody();
        if (!user.getUserRoles().contains(UserRoleTO.STAFF) || StringUtils.isBlank(user.getId())) {
            throw new TppException("Only TPPs are allowed to query this data", 403);
        }
        return user.getId();
    }
}
