package de.adorsys.ledgers.oba.rest.server.service;

import org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import static org.adorsys.ledgers.consent.psu.rest.client.CmsPsuAisClient.DEFAULT_SERVICE_INSTANCE_ID;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConsentServiceTest {
    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    @InjectMocks
    private ConsentService consentService;
    @Mock
    private CmsPsuAisClient cmsPsuAisClient;

    @Test
    public void revokeConsentSuccess() {
        when(cmsPsuAisClient.revokeConsent(CONSENT_ID, null, null, null, null, DEFAULT_SERVICE_INSTANCE_ID)).thenReturn(ResponseEntity.ok(Boolean.TRUE));

        assertTrue(consentService.revokeConsent(CONSENT_ID));
    }
}
