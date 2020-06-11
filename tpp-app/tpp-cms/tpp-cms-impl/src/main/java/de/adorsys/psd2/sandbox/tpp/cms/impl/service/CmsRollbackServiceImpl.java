package de.adorsys.psd2.sandbox.tpp.cms.impl.service;

import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsRollbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CmsRollbackServiceImpl implements CmsRollbackService {
    private final EntityManager entityManager;
    private final ResourceLoader loader;

    private static final String ROLLBACK_CMS = "classpath:rollbackCms.sql";

    @Override
    @Transactional
    public void revertDatabase(List<String> userIds, LocalDateTime databaseStateDateTime) {
        executeNativeQuery(ROLLBACK_CMS, userIds, databaseStateDateTime);
    }

    private void executeNativeQuery(String queryFilePath, List<String> userIds, LocalDateTime databaseStateDateTime) {
        try {
            InputStream stream = loader.getResource(queryFilePath).getInputStream();
            String query = IOUtils.toString(stream, StandardCharsets.UTF_8);
            entityManager.createNativeQuery(query)
                .setParameter(1, userIds)
                .setParameter(2, databaseStateDateTime)
                .executeUpdate();
        } catch (IOException e) {
            throw new RuntimeException("Error while loading CMS SQL query file.");
        }
    }

}
