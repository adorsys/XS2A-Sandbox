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

package de.adorsys.psd2.sandbox.cms.connector.impl.service;

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
class CmsDbNativeServiceImplTest {

    @InjectMocks
    private CmsDbNativeServiceImpl cmsDbNativeService;

    @Mock
    private final EntityManager cmsEntityManager = mock(EntityManager.class);
    @Mock
    private ResourceLoader loader;

    @BeforeEach
    void prepare() {
        cmsDbNativeService.setCmsEntityManager(cmsEntityManager);
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
            cmsDbNativeService.revertDatabase(Arrays.asList("anton.brueckner", "max.musterman"), LocalDateTime.now());
        } catch (Exception e) {
            fail("Should not be any exceptions");
        }
    }

    @Test
    void deleteConsents() {
        // Given
        Query query = mock(Query.class);

        when(cmsEntityManager.createNativeQuery(anyString()))
            .thenReturn(query);
        when(query.setParameter(anyInt(), any()))
            .thenReturn(query);
        when(cmsEntityManager.createNativeQuery(anyString()))
            .thenReturn(query);

        // When
        try {
            cmsDbNativeService.deleteConsentsByUserIds(Arrays.asList("anton.brueckner", "max.musterman"));
        } catch (Exception e) {
            fail("Should not be any exceptions");
        }
    }
}
