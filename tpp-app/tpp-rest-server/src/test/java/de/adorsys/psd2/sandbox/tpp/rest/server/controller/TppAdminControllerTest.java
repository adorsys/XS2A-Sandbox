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

package de.adorsys.psd2.sandbox.tpp.rest.server.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserExtendedTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserRoleTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.AdminRestClient;
import de.adorsys.ledgers.middleware.client.rest.DataRestClient;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtStaffRestClient;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import de.adorsys.psd2.sandbox.tpp.cms.api.service.CmsDbNativeService;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.User;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserRole;
import de.adorsys.psd2.sandbox.tpp.rest.server.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TppAdminControllerTest {

    public static final String USER_LOGIN = "batman";
    public static final String TPP_ID = "UU1234567";

    private static final byte[] EMPTY_BODY = new byte[0];

    @InjectMocks
    private TppAdminController controller;

    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @Mock
    private DataRestClient dataRestClient;
    @Mock
    private AdminRestClient adminRestClient;
    @Mock
    private UserMgmtStaffRestClient userMgmtStaffRestClient;
    @Mock
    private CmsDbNativeService cmsDbNativeService;

    private MockMvc mockMvc;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new ParameterNamesModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                      .setMessageConverters(new MappingJackson2HttpMessageConverter(mapper))
                      .build();
    }

    @Test
    void users() throws Exception {
        CustomPageImpl<UserExtendedTO> body = new CustomPageImpl<>();
        UserExtendedTO userExtendedTO = new UserExtendedTO();
        userExtendedTO.setLogin(USER_LOGIN);
        body.setContent(Collections.singletonList(userExtendedTO));
        when(adminRestClient.users("country", TPP_ID, "tppLogin", "userLogin", UserRoleTO.STAFF, true, 1, 20))
            .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(MockMvcRequestBuilders.get("/tpp/admin/users")
                            .param("country", "country")
                            .param("tppId", TPP_ID)
                            .param("tppLogin", "tppLogin")
                            .param("userLogin", "userLogin")
                            .param("role", UserRoleTO.STAFF.name())
                            .param("blocked", "true")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(equalToIgnoringCase(mapper.writeValueAsString(body))));
    }

    @Test
    void user() throws Exception {
        UserTO userTO = new UserTO();
        userTO.setLogin(USER_LOGIN);

        when(adminRestClient.user(userTO)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(MockMvcRequestBuilders.put("/tpp/admin/users")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(userTO)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().bytes(EMPTY_BODY));
    }

    @Test
    void accounts() throws Exception {
        CustomPageImpl<AccountDetailsExtendedTO> body = new CustomPageImpl<>();
        AccountDetailsExtendedTO accountDetailsExtendedTO = new AccountDetailsExtendedTO();
        accountDetailsExtendedTO.setBranchLogin(USER_LOGIN);
        body.setContent(Collections.singletonList(accountDetailsExtendedTO));

        when(adminRestClient.accounts("country", TPP_ID, "tppLogin", "ibanParam", true, 1, 20))
            .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(MockMvcRequestBuilders.get("/tpp/admin/account")
                            .param("country", "country")
                            .param("tppId", TPP_ID)
                            .param("tppLogin", "tppLogin")
                            .param("ibanParam", "ibanParam")
                            .param("blocked", "true")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(equalToIgnoringCase(mapper.writeValueAsString(body))));
    }

    @Test
    void register() throws Exception {
        User user = new User();
        user.setLogin(USER_LOGIN);
        user.setUserRoles(Collections.singletonList(UserRole.CUSTOMER));

        mockMvc.perform(MockMvcRequestBuilders.post("/tpp/admin/register")
                            .param("tppId", TPP_ID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(user)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().bytes(EMPTY_BODY));

        UserTO userTO = userMapper.toUserTO(user);
        userTO.setBranch(TPP_ID);
        verify(adminRestClient, times(1)).register(userTO);
    }

    @Test
    void admin() throws Exception {
        User user = new User();
        user.setLogin(USER_LOGIN);
        UserTO userTO = userMapper.toUserTO(user);
        userTO.setUserRoles(Collections.singletonList(UserRoleTO.SYSTEM));

        when(adminRestClient.register(userTO)).thenReturn(ResponseEntity.ok(userTO));

        mockMvc.perform(MockMvcRequestBuilders.post("/tpp/admin/register/admin")
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .content(mapper.writeValueAsString(user)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(content().bytes(EMPTY_BODY));

        verify(adminRestClient, times(1)).register(userTO);
    }

    @Test
    void admins() throws Exception {
        CustomPageImpl<UserTO> body = new CustomPageImpl<>();
        UserTO userTO = new UserTO();
        userTO.setLogin(USER_LOGIN);
        body.setContent(Collections.singletonList(userTO));

        when(adminRestClient.admins(1, 20)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(MockMvcRequestBuilders.get("/tpp/admin/admins")
                            .param("page", "1")
                            .param("size", "20")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string(equalToIgnoringCase(mapper.writeValueAsString(body))));
    }

    @Test
    void remove() throws Exception {
        when(userMgmtStaffRestClient.getBranchUserLoginsByBranchId(TPP_ID)).thenReturn(ResponseEntity.ok(Collections.singletonList(USER_LOGIN)));

        mockMvc.perform(MockMvcRequestBuilders.delete("/tpp/admin")
                            .param("tppId", TPP_ID)
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andExpect(content().bytes(EMPTY_BODY));

        verify(dataRestClient, times(1)).branch(TPP_ID);
        verify(cmsDbNativeService, times(1)).deleteConsentsByUserIds(Collections.singletonList(USER_LOGIN));
    }

    @Test
    void removeAllTestData() throws Exception {
        CustomPageImpl<UserExtendedTO> body = new CustomPageImpl<>();

        UserExtendedTO userExtendedTO1 = new UserExtendedTO();
        userExtendedTO1.setId("111");
        userExtendedTO1.setLogin(USER_LOGIN);

        UserExtendedTO userExtendedTO2 = new UserExtendedTO();
        userExtendedTO2.setId("222");
        userExtendedTO2.setLogin("123456789");

        body.setContent(List.of(userExtendedTO1, userExtendedTO2));

        when(adminRestClient.users(null, null, null, null, UserRoleTO.STAFF, null, 0, 9999))
            .thenReturn(ResponseEntity.ok(body));
        when(userMgmtStaffRestClient.getBranchUserLoginsByBranchId("222")).thenReturn(ResponseEntity.ok(Collections.singletonList(USER_LOGIN)));


        mockMvc.perform(MockMvcRequestBuilders.delete("/tpp/admin/test/data")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andExpect(content().bytes(EMPTY_BODY));

        verify(dataRestClient, times(1)).branch("222");
        verify(cmsDbNativeService, times(1)).deleteConsentsByUserIds(Collections.singletonList(USER_LOGIN));
    }

    @Test
    void removeAllTestData_responseIsNull() throws Exception {
        when(adminRestClient.users(null, null, null, null, UserRoleTO.STAFF, null, 0, 9999))
            .thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/tpp/admin/test/data")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isNoContent())
            .andExpect(content().bytes(EMPTY_BODY));

        verifyNoInteractions(dataRestClient, userMgmtStaffRestClient, cmsDbNativeService);
    }

    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/tpp/admin/password")
                            .param("tppId", TPP_ID)
                            .param("pin", "password123")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().bytes(EMPTY_BODY));

        verify(adminRestClient, times(1)).updatePassword(TPP_ID, "password123");
    }

    @Test
    void changeStatus() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/tpp/admin/status")
                            .param("userId", "userId")
                            .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().bytes(EMPTY_BODY));

        verify(adminRestClient, times(1)).changeStatus("userId");
    }
}
