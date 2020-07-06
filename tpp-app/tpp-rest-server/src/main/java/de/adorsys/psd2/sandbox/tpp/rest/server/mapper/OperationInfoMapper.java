package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationInfoEntity;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.OperationInfo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationInfoMapper {
    OperationInfo toOperationInfo(OperationInfoEntity source);

    List<OperationInfo> toOperationInfos(List<OperationInfoEntity> source);

    OperationInfoEntity toOperationInfoEntity(OperationInfo source);
}
