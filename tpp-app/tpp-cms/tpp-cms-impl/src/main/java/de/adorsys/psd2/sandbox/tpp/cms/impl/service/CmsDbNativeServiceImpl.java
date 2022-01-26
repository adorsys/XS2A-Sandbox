/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsDbNativeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmsDbNativeServiceImpl implements CmsDbNativeService {
    private final ResourceLoader loader;

    private static final String ROLLBACK_CMS = "classpath:rollbackCms.sql";
    private static final String DELETE_CONSENTS_IN_CMS = "classpath:deleteConsents.sql";

    private EntityManager cmsEntityManager;

    @Qualifier("cmsEntityManager")
    @Autowired
    public void setCmsEntityManager(EntityManager cmsEntityManager) {
        this.cmsEntityManager = cmsEntityManager;
    }

    @Override
    @Transactional("cmsTransactionManager")
    public void revertDatabase(List<String> userIds, LocalDateTime databaseStateDateTime) {
        log.debug("Reverting CMS DB for users: " + Arrays.toString(userIds.toArray()) + ", timestamp: " + databaseStateDateTime.toString());
        cmsEntityManager.createNativeQuery(loadQueryFromFile(ROLLBACK_CMS))
            .setParameter(1, userIds)
            .setParameter(2, databaseStateDateTime)
            .executeUpdate();
    }

    @Override
    @Transactional("cmsTransactionManager")
    public void deleteConsentsByUserIds(List<String> userIds) {
        log.debug("Deleting AIS-specific data in CMS DB (if present) for users: " + Arrays.toString(userIds.toArray()));

        cmsEntityManager.createNativeQuery(loadQueryFromFile(DELETE_CONSENTS_IN_CMS))
            .setParameter(1, userIds)
            .executeUpdate();
    }

    @SuppressWarnings({"PMD.AvoidThrowingRawExceptionTypes", "java:S112"})
    private String loadQueryFromFile(String filename) {
        try {
            InputStream stream = loader.getResource(filename).getInputStream();
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading CMS SQL query from file: " + filename);
        }
    }
}
