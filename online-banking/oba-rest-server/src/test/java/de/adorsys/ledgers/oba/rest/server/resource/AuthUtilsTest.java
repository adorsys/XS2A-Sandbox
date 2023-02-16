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

package de.adorsys.ledgers.oba.rest.server.resource;

import de.adorsys.ledgers.middleware.api.domain.sca.GlobalScaResponseTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.oba.service.api.domain.exception.ObaException;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthUtilsTest {

    private static final String LOGIN = "anton.brueckner";

    @Test
    void checkIfUserInitiatedOperation() {
        AuthUtils.checkIfUserInitiatedOperation(getLoginResult(), Collections.singletonList(getPsuIdData(LOGIN)));
    }

    @Test
    void checkIfUserInitiatedOperation_fail() {
        GlobalScaResponseTO loginResult = getLoginResult();
        List<PsuIdData> wrongLogin = List.of(getPsuIdData("some wrong login"));
        // Then
        assertThrows(ObaException.class, () -> AuthUtils.checkIfUserInitiatedOperation(loginResult, wrongLogin));
    }

    private PsuIdData getPsuIdData(String login) {
        return new PsuIdData(login, null, null, null, null);
    }

    private GlobalScaResponseTO getLoginResult() {
        GlobalScaResponseTO result = new GlobalScaResponseTO();
        BearerTokenTO token = new BearerTokenTO();
        AccessTokenTO acc = new AccessTokenTO();
        acc.setLogin(LOGIN);
        token.setAccessTokenObject(acc);
        result.setBearerToken(token);
        return result;
    }
}
