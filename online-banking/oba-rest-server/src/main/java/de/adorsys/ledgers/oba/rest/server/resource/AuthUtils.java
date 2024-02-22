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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.psd2.sandbox.auth.MiddlewareAuthentication;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaErrorCode;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class AuthUtils {

    private AuthUtils() {
    }

    public static String psuId(MiddlewareAuthentication auth) {
        if (auth == null) {
            return null;
        }
        return psuId(auth.getBearerToken());
    }

    public static String psuId(BearerTokenTO bearerToken) {
        return bearerToken.getAccessTokenObject().getLogin();
    }

    public static void checkIfUserInitiatedOperation(GlobalScaResponseTO loginResult, List<PsuIdData> psuIdData) {
        if (CollectionUtils.isNotEmpty(psuIdData)) {
            AuthUtils.checkUserInitiatedProcedure(psuIdData.get(0).getPsuId(), loginResult.getBearerToken());
        }
    }

    private static void checkUserInitiatedProcedure(String loginFromRequest, BearerTokenTO bearerToken) {
        if (!psuId(bearerToken).equals(loginFromRequest)) {
            throw ObaException.builder()
                      .obaErrorCode(ObaErrorCode.LOGIN_FAILED)
                      .devMessage("Operation you're logging in is not meant for current user")
                      .build();
        }
    }
}
