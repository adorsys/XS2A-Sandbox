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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.ScaUserDataMixedIn;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;
import java.util.List;

class ObjectMapperMixInTest {

    @Test
    void scaUserDataMixIn() throws JsonProcessingException, JSONException {
        // Given
        String expected = "{\"id\":\"id\",\"login\":\"login\",\"email\":\"email\",\"pin\":\"pin\",\"scaUserData\":[{\"id\":\"id\",\"scaMethod\":\"SMTP_OTP\",\"methodValue\":\"methodValue\",\"user\":null,\"usesStaticTan\":true,\"staticTan\":\"STATIC TAN\", \"decoupled\":false, \"valid\":false}],\"accountAccesses\":[],\"userRoles\":[],\"branch\":\"branch\",\"blocked\":false,\"systemBlocked\":false}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ScaUserDataTO.class, ScaUserDataMixedIn.class);

        UserTO user = getUser();

        // When
        String result = objectMapper.writeValueAsString(user);

        // Then
        JSONAssert.assertEquals(result, expected, true);
    }

    private UserTO getUser() {
        return new UserTO("id", "login", "email", "pin", getScaUserData(), Collections.emptyList(), Collections.emptyList(), "branch", false, false);
    }

    private List<ScaUserDataTO> getScaUserData() {
        return Collections.singletonList(new ScaUserDataTO("id", ScaMethodTypeTO.SMTP_OTP, "methodValue", null, true, "STATIC TAN", false, false));
    }
}
