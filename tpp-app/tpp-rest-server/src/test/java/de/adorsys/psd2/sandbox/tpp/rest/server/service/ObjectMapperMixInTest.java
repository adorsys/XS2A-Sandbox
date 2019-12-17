package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.middleware.api.domain.um.ScaMethodTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTypeTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.ScaUserDataMixedIn;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;
import java.util.List;

public class ObjectMapperMixInTest {

    @Test
    public void scaUserDataMixIn() throws JsonProcessingException, JSONException {
        String expected = "{\"id\":\"id\",\"login\":\"login\",\"email\":\"email\",\"pin\":\"pin\",\"scaUserData\":[{\"id\":\"id\",\"scaMethod\":\"EMAIL\",\"methodValue\":\"methodValue\",\"user\":null,\"usesStaticTan\":true,\"staticTan\":\"STATIC TAN\", \"decoupled\":false}],\"accountAccesses\":[],\"userRoles\":[],\"branch\":\"branch\",\"userType\":\"FAKE\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.addMixIn(ScaUserDataTO.class, ScaUserDataMixedIn.class);

        UserTO user = getUser();
        String result = objectMapper.writeValueAsString(user);
        JSONAssert.assertEquals(result, expected, true);
    }

    private UserTO getUser() {
        return new UserTO("id", "login", "email", "pin", getScaUserData(), Collections.emptyList(), Collections.emptyList(), "branch", UserTypeTO.FAKE);
    }

    private List<ScaUserDataTO> getScaUserData() {
        return Collections.singletonList(new ScaUserDataTO("id", ScaMethodTypeTO.EMAIL, "methodValue", null, true, "STATIC TAN", false));
    }
}
