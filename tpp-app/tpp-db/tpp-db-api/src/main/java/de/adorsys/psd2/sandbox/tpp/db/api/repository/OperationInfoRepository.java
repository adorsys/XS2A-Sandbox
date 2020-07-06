package de.adorsys.psd2.sandbox.tpp.db.api.repository;

import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationInfoEntity;
import de.adorsys.psd2.sandbox.tpp.db.api.domain.OperationTypeEntity;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface OperationInfoRepository extends PagingAndSortingRepository<OperationInfoEntity, Long> {

    List<OperationInfoEntity> findAllByTppIdAndOperationTypeOrderByCreatedDesc(String tppId, OperationTypeEntity operationType);

    List<OperationInfoEntity> findAllByTppIdOrderByCreatedDesc(String tppId);

    boolean existsByIdAndTppId(Long id, String tppId);
}
