package de.adorsys.psd2.sandbox.tpp.cms.api.service;

import java.time.LocalDateTime;
import java.util.List;

public interface CmsRollbackService {
    void revertDatabase(List<String> userIds, LocalDateTime databaseStateDateTime);
}
