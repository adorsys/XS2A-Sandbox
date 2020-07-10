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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TppOperationInfoServiceTest {

    @InjectMocks
    TppOperationInfoService service;

    @Mock
    private OperationInfoRepository infoRepository;
    @Mock
    private UserMgmtRestClient userMgmtRestClient;
    @Mock
    private OperationInfoMapper infoMapper;

    private static final OperationInfoMapper MAPPER = Mappers.getMapper(OperationInfoMapper.class);

    @Test
    void getOperationsByTypeAndTppId() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUser(UserRoleTO.STAFF)));
        when(infoRepository.findAllByTppIdAndOperationTypeOrderByCreatedDesc(anyString(), any())).thenReturn(getOperationInfos());
        when(infoMapper.toOperationInfos(anyList())).thenAnswer(a -> MAPPER.toOperationInfos(a.getArgument(0)));
        List<OperationInfo> result = service.getOperationsByTypeAndTppId(OperationType.CONSENT);
        assertEquals(MAPPER.toOperationInfos(getOperationInfos()), result);
    }

    @Test
    void getOperationsByTypeAndTppId_user_is_not_tpp() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUser(UserRoleTO.CUSTOMER)));
        assertThrows(TppException.class, () -> service.getOperationsByTypeAndTppId(OperationType.CONSENT));
    }

    @Test
    void createInfo() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUser(UserRoleTO.STAFF)));
        when(infoMapper.toOperationInfoEntity(any())).thenAnswer(a -> MAPPER.toOperationInfoEntity(a.getArgument(0)));
        OperationInfo result = service.createInfo(getInfo().iterator().next());

        ArgumentCaptor<OperationInfoEntity> captor = ArgumentCaptor.forClass(OperationInfoEntity.class);
        verify(infoRepository, times(1)).save(captor.capture());
        assertEquals("branchId", captor.getValue().getTppId());
    }

    @Test
    void deleteInfo() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUser(UserRoleTO.STAFF)));
        when(infoRepository.existsByIdAndTppId(anyLong(), anyString())).thenReturn(true);

        service.deleteInfo(123L);

        verify(infoRepository, times(1)).existsByIdAndTppId(anyLong(), anyString());
        verify(infoRepository, times(1)).deleteById(123L);
    }

    @Test
    void deleteInfo_info_belongs_to_other_or_non_existent() {
        when(userMgmtRestClient.getUser()).thenReturn(ResponseEntity.ok(getUser(UserRoleTO.STAFF)));
        when(infoRepository.existsByIdAndTppId(anyLong(), anyString())).thenReturn(false);

        assertThrows(TppException.class, () -> service.deleteInfo(123L));

        verify(infoRepository, times(1)).existsByIdAndTppId(anyLong(), anyString());
        verify(infoRepository, times(0)).deleteById(123L);
    }

    private List<OperationInfoEntity> getOperationInfos() {
        OperationInfoEntity info = new OperationInfoEntity();
        info.setId(123);
        info.setTppId("branchId");
        info.setEncryptedOperationId("XYZ");
        info.setOperationType(OperationTypeEntity.CONSENT);
        return Collections.singletonList(info);
    }

    private List<OperationInfo> getInfo() {
        return MAPPER.toOperationInfos(getOperationInfos());
    }

    private UserTO getUser(UserRoleTO role) {
        UserTO user = new UserTO();
        user.setUserRoles(Collections.singletonList(role));
        user.setId(role == UserRoleTO.STAFF ? "branchId" : "someOtherId");
        user.setBranch("branchId");
        return user;
    }
}
