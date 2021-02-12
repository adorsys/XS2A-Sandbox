package de.adorsys.ledgers.oba.service.impl.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.UsageTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationTemplate;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Currency;

import static de.adorsys.ledgers.middleware.api.domain.account.AccountStatusTO.ENABLED;
import static de.adorsys.ledgers.middleware.api.domain.account.AccountTypeTO.CASH;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ObaAisConsentMapperTest {
    private static final String CONSENT_ID = "234234kjlkjklj2lk34j";
    private static final String IBAN = "DE1234567890";
    private static final Currency EUR = Currency.getInstance("EUR");

    @InjectMocks
    private ObaAisConsentMapper obaAisConsentMapper;

    @Test
    void toTo() {
        // When
        AisConsentTO result = obaAisConsentMapper.toTo(getCmsAisAccountConsent());

        // Then
        assertEquals(getAisConsentTO(), result);
    }

    @Test
    void accountAccess() {
        // When
        AisAccountAccess result = obaAisConsentMapper.accountAccess(getAisAccountAccessInfoTO(), Collections.singletonList(getAccountDetailsTO()));

        // Then
        assertEquals(getAisAccountAccess(), result);
    }

    private AccountDetailsTO getAccountDetailsTO() {
        return new AccountDetailsTO("id", IBAN, "bban", "pan", "maskedPan", "msisdn", EUR, "name", "product", CASH, ENABLED, "bic", "linkedAccounts", UsageTypeTO.PRIV, "details", Collections.EMPTY_LIST, false, false, BigDecimal.ZERO, null);
    }

    private AisConsentTO getAisConsentTO() {
        return new AisConsentTO(CONSENT_ID, "userId", "tppId", 3, getAisAccountAccessInfoTO(), LocalDate.now().plusMonths(1), false);
    }

    private AisAccountAccessInfoTO getAisAccountAccessInfoTO() {
        return new AisAccountAccessInfoTO(Collections.singletonList(IBAN), Collections.EMPTY_LIST, Collections.EMPTY_LIST, AisAccountAccessTypeTO.ALL_ACCOUNTS, AisAccountAccessTypeTO.ALL_ACCOUNTS);
    }

    private CmsAisAccountConsent getCmsAisAccountConsent() {
        return new CmsAisAccountConsent(CONSENT_ID, getAisAccountAccess(), false, LocalDate.now().plusMonths(1), LocalDate.now().plusMonths(1), 3, LocalDate.now(), ConsentStatus.VALID, false, false,
                                        AisConsentRequestType.GLOBAL, Collections.singletonList(getPsuIdData()), getTppInfo(), new AuthorisationTemplate(), false, Collections.emptyList(),
                                        Collections.emptyMap(), OffsetDateTime.MIN, OffsetDateTime.MIN, null, null);
    }

    private TppInfo getTppInfo() {
        TppInfo info = new TppInfo();
        info.setAuthorisationNumber("tppId");
        return info;
    }

    private PsuIdData getPsuIdData() {
        return new PsuIdData("userId", "psuIdType", "psuCorporateId", "psuCorporateIdType", "psuIpAddress");
    }

    private AisAccountAccess getAisAccountAccess() {
        return new AisAccountAccess(Collections.singletonList(getReference()), Collections.emptyList(), Collections.emptyList(), "ALL_ACCOUNTS", "ALL_ACCOUNTS", null, null);
    }

    private AccountReference getReference() {
        AccountReference reference = new AccountReference();
        reference.setIban(IBAN);
        reference.setCurrency(EUR);
        reference.setAspspAccountId("id");
        reference.setBban("bban");
        reference.setPan("pan");
        reference.setMaskedPan("maskedPan");
        reference.setMsisdn("msisdn");
        return reference;
    }
}
