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
public class CmsDbDbNativeServiceImpl implements CmsDbNativeService {
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

    @SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
    private String loadQueryFromFile(String filename) {
        try {
            InputStream stream = loader.getResource(filename).getInputStream();
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading CMS SQL query from file: " + filename);
        }
    }
}
