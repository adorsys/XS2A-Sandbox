/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

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
        if (user == null || !user.getUserRoles().contains(UserRoleTO.STAFF) || StringUtils.isBlank(user.getId())) {
            throw new TppException("Only TPPs are allowed to query this data", 403);
        }
        return user.getId();
    }
}
