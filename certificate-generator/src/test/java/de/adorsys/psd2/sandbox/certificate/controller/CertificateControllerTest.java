package de.adorsys.psd2.sandbox.certificate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.sandbox.certificate.config.CorsConfigProperties;
import de.adorsys.psd2.sandbox.certificate.model.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.model.CertificateResponse;
import de.adorsys.psd2.sandbox.certificate.model.PspRole;
import de.adorsys.psd2.sandbox.certificate.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = CertificateController.class)
class CertificateControllerTest {
    private static final String CERTIFICATE = "-----BEGIN CERTIFICATE-----Stuff-----END CERTIFICATE-----";
    private static final String PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----Stuff-----END RSA PRIVATE KEY-----";

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CertificateService certificateService;

    @MockBean
    private CorsConfigProperties properties;

    @Test
    void createCert() throws Exception {
        // Given
        CertificateResponse certificateResponse = CertificateResponse.builder()
                                                      .encodedCert(CERTIFICATE)
                                                      .privateKey(PRIVATE_KEY)
                                                      .build();

        given(certificateService.newCertificate(any())).willReturn(certificateResponse);

        // When
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("87B2AC")
                                                    .commonName("XS2A Sandbox")
                                                    .organizationName("Fictional Corporation AG")
                                                    .roles(Collections.singletonList(PspRole.AISP))
                                                    .build();

        // Then
        mockMvc.perform(
            post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.encodedCert").value(CERTIFICATE))
            .andExpect(jsonPath("$.privateKey").value(PRIVATE_KEY));
    }

    @Test
    void createCertWithoutRoles() throws Exception {
        // Given
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("87B2AC")
                                                    .commonName("XS2A Sandbox")
                                                    .organizationName("Fictional Corporation AG")
                                                    .build();

        // When
        MockHttpServletResponse response = mockMvc.perform(
            post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateRequest)))
                                               .andExpect(status().isBadRequest())
                                               .andReturn().getResponse();
        // Then
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    void createCertWithoutAuthrizationNumber() throws Exception {
        // Given
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .organizationName("Fictional Corporation AG")
                                                    .commonName("XS2A Sandbox")
                                                    .roles(Collections.singletonList(PspRole.AISP))
                                                    .build();

        // When
        MockHttpServletResponse response = mockMvc.perform(
            post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateRequest)))
                                               .andExpect(status().isBadRequest())
                                               .andReturn().getResponse();

        // Then
        assertThat(response.getContentAsString(), is(""));
    }

    @Test
    void createCertWithoutOrganizationName() throws Exception {
        // Given
        CertificateRequest certificateRequest = CertificateRequest.builder()
                                                    .authorizationNumber("87B2AC")
                                                    .commonName("XS2A Sandbox")
                                                    .roles(Collections.singletonList(PspRole.AISP))
                                                    .build();
        // When
        MockHttpServletResponse response = mockMvc.perform(
            post("/api/cert-generator").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(certificateRequest)))
                                               .andExpect(status().isBadRequest())
                                               .andReturn().getResponse();

        // Then
        assertThat(response.getContentAsString(), is(""));
    }
}

