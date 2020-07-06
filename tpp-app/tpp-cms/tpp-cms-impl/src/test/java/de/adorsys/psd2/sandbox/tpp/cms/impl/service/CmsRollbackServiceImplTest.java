package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CmsRollbackServiceImplTest {

    @InjectMocks
    private CmsRollbackServiceImpl cmsRollbackService;

    @Mock
    private final EntityManager cmsEntityManager = mock(EntityManager.class);
    @Mock
    private ResourceLoader loader;

    @BeforeEach
    void prepare() {
        cmsRollbackService.setCmsEntityManager(cmsEntityManager);
    }

    @Test
    void revertDatabase() {
        // Given
        when(loader.getResource(anyString()))
            .thenReturn(new ByteArrayResource("select * from consent.consent;".getBytes()));

        Query query = mock(Query.class);

        when(cmsEntityManager.createNativeQuery(anyString()))
            .thenReturn(query);
        when(query.setParameter(anyInt(), any()))
            .thenReturn(query);
        when(query.setParameter(anyInt(), any()))
            .thenReturn(query);
        when(cmsEntityManager.createNativeQuery(anyString()))
            .thenReturn(query);

        // When
        try {
            cmsRollbackService.revertDatabase(Arrays.asList("anton.brueckner", "max.musterman"), LocalDateTime.now());
        } catch (Exception e) {
            fail("Should not be any exceptions");
        }
    }
}
