package de.adorsys.psd2.sandbox.tpp.cms.api.service;

import java.time.LocalDateTime;
import java.util.List;

public interface CmsDbNativeService {

    /**
     * Deletes all data in CMS database (AIS-specific, PIS-specific and authorisation data) by the given user IDs (PSU IDs) and timestamp.
     *
     * @param userIds               PSU IDs in CMS (like 'anton.brueckner').
     * @param databaseStateDateTime timestamp.
     */
    void revertDatabase(List<String> userIds, LocalDateTime databaseStateDateTime);

    /**
     * Deletes all AIS-specific data in CMS database for the given PSU IDs.
     *
     * @param userIds PSU IDs in CMS (like 'anton.brueckner').
     */
    void deleteConsentsByUserIds(List<String> userIds);
}
