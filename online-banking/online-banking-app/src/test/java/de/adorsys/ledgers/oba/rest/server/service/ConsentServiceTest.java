package de.adorsys.ledgers.oba.rest.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.ledgers.oba.service.impl.service.ConsentServiceImpl;
import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTest {
    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    private static final String TOKEN = "Bearer QWERTY";
    @InjectMocks
    private ConsentServiceImpl consentService;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void revokeConsentSuccess() {
        when(cmsPsuAisClient.revokeConsent(CONSENT_ID, null, null, null, null, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(ResponseEntity.ok(Boolean.TRUE));

        assertTrue(consentService.revokeConsent(CONSENT_ID));
    }
}
