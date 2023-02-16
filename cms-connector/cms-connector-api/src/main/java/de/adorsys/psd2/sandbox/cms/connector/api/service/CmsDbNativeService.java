/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.cms.connector.api.service;

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
