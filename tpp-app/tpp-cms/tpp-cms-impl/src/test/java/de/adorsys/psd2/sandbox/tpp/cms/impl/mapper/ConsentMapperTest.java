package de.adorsys.psd2.sandbox.tpp.cms.impl.mapper;

import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.core.data.AccountAccess;
import de.adorsys.psd2.core.mapper.ConsentDataMapper;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.*;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.consent.ConsentTppInformation;
import de.adorsys.psd2.xs2a.core.consent.ConsentType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConsentMapperTest {
    private static final Integer ALLOWED_FREQUENCY = 5;
    private static final int REQUESTED_FREQUENCY = 5;
    private static final LocalDate VALID_UNTIL = LocalDate.of(2019, 8, 3);
    private static final String PSU_ID = "PSU-ID";
    private static final String PSU_ID_TYPE = "PSU-ID-TYPE";
    private static final String CORPORATE_ID = "PSU-CORPORATE-ID";
    private static final String CORPORATE_ID_TYPE = "PSU-CORPORATE-ID-TYPE";
    private static final String AUTH_NR = "AUTH NUMBER";
    private static final String TPP_NAME = "TEST TPP";
    private static final String AUTH_ID = "1234567890";
    private static final String AUTH_NAME = "AUTHORITY NAME";
    private static final String COUNTRY = "Germany";
    private static final String ORGANISATION = "Some Organisation GmbH";
    private static final String ORGANISATION_UNIT = "Some Organisation Unit";
    private static final String CITY = "Nurnberg";
    private static final String STATE = "Bavaria";
    private static final TppRedirectUri TPP_CANCEL_URI = new TppRedirectUri("http://gmail.com", "http://unknown.com");
    private static final ThirdPartyRedirectUri TP_REDIRECT_URI = new ThirdPartyRedirectUri("http://adorsys.de", "http://google.com");
    private static final ThirdPartyRedirectUri TP_CANCEL_URI = new ThirdPartyRedirectUri("http://gmail.com", "http://unknown.com");
    private static final String ISSUER_CN = "Some issuer";
    private static final String ACC1_ID = "DE12345";
    private static final String ASPSP_ACC1_ID = "ZTD12345";
    private static final String CURRENCY_STR = "EUR";
    private static final String ACC1_RESOURCE_ID = "RES1ACC";
    private static final String ACC2_ID = "DE54321";
    private static final String ASPSP_ACC2_ID = "ZTD54321";
    private static final String ACC2_RESOURCE_ID = "RES2ACC";

    @InjectMocks
    private ConsentMapper mapper;

    @Mock
    private ConsentDataMapper consentDataMapper;

    // TODO complete all test cases, but before that move object creation to yaml or json https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/592
    @Test
    public void toCmsAisConsentRequest() {
        CmsConsent expectedResult = getCmsAisConsentRequest();
        CmsConsent result = mapper.mapToCmsConsent(getAisConsent());

        Assert.assertEquals(result.getConsentStatus(), expectedResult.getConsentStatus());
        Assert.assertEquals(result.getConsentType(), expectedResult.getConsentType());

        Assert.assertEquals(expectedResult.getTppInformation().getTppInfo(), result.getTppInformation().getTppInfo());
    }

    //TPP-UI Server entities
    private AisConsent getAisConsent() {
        AisConsent consent = new AisConsent();
        consent.setPsuInfo(getPsuInfo());
        consent.setTppInfo(getTppInfo());
        consent.setAllowedFrequencyPerDay(ALLOWED_FREQUENCY);
        consent.setRequestedFrequencyPerDay(REQUESTED_FREQUENCY);
        consent.setAccess(getAccess());
        consent.setValidUntil(VALID_UNTIL);
        consent.setRecurringIndicator(true);
        consent.setTppRedirectPreferred(true);
        consent.setCombinedServiceIndicator(true);
        consent.setAllPsd2(UserAccountAccessType.ALL_ACCOUNTS);
        consent.setAvailableAccounts(UserAccountAccessType.ALL_ACCOUNTS);
        consent.setAvailableAccountsWithBalance(UserAccountAccessType.ALL_ACCOUNTS);
        return consent;
    }

    private PsuIdDataInfo getPsuInfo() {
        PsuIdDataInfo psu = new PsuIdDataInfo();
        psu.setPsuId(PSU_ID);
        psu.setPsuIdType(PSU_ID_TYPE);
        psu.setPsuCorporateId(CORPORATE_ID);
        psu.setPsuCorporateIdType(CORPORATE_ID_TYPE);
        return psu;
    }

    private ThirdPartyInfo getTppInfo() {
        ThirdPartyInfo info = new ThirdPartyInfo();
        info.setAuthorisationNumber(AUTH_NR);
        info.setTppName(TPP_NAME);
        info.setTppRoles(Arrays.asList(ThirdPartyRole.values()));
        info.setAuthorityId(AUTH_ID);
        info.setAuthorityName(AUTH_NAME);
        info.setCountry(COUNTRY);
        info.setOrganisation(ORGANISATION);
        info.setOrganisationUnit(ORGANISATION_UNIT);
        info.setCity(CITY);
        info.setState(STATE);
        info.setCancelTppRedirectUri(TP_REDIRECT_URI);
        info.setCancelTppRedirectUri(TP_CANCEL_URI);
        info.setIssuerCN(ISSUER_CN);
        return info;
    }

    private AccountAccessInfo getAccess() {
        AccountAccessInfo access = new AccountAccessInfo();
        access.setAccounts(getAccounts());
        access.setBalances(getAccounts());
        access.setTransactions(getAccounts());
        return access;
    }

    private List<AccountReferenceInfo> getAccounts() {
        AccountReferenceInfo[] arr = {
            AccountReferenceInfo.builder()
                .accountIdentifier(ACC1_ID)
                .accountType(UserAccountReferenceType.IBAN)
                .aspspAccountId(ASPSP_ACC1_ID)
                .currency(CURRENCY_STR)
                .resourceId(ACC1_RESOURCE_ID)
                .build(),
            AccountReferenceInfo.builder()
                .accountIdentifier(ACC2_ID)
                .accountType(UserAccountReferenceType.IBAN)
                .aspspAccountId(ASPSP_ACC2_ID)
                .currency(CURRENCY_STR)
                .resourceId(ACC2_RESOURCE_ID)
                .build()
        };
        return Arrays.asList(arr);
    }

    //CMS entities
    private CmsConsent getCmsAisConsentRequest() {
        CmsConsent consent = new CmsConsent();
        consent.setPsuIdDataList(Collections.singletonList(getPsuData()));
        consent.setTppInformation(buildConsentTppInformation());
        consent.setTppAccountAccesses(geAccountAccess());
        consent.setValidUntil(VALID_UNTIL);
        consent.setRecurringIndicator(true);
        consent.setConsentStatus(ConsentStatus.RECEIVED);
        consent.setConsentType(ConsentType.AIS);
        return consent;
    }

    private AccountAccess geAccountAccess() {
        return new AccountAccess(new ArrayList<>(buildAccountReference()),
                                 new ArrayList<>(buildAccountReference()),
                                 new ArrayList<>(buildAccountReference()),
                                 null);
    }

    private List<AccountReference> buildAccountReference() {
        return Arrays.asList(
            new AccountReference(AccountReferenceType.IBAN, ACC1_ID, Currency.getInstance("EUR"), ACC1_RESOURCE_ID, ASPSP_ACC1_ID),
            new AccountReference(AccountReferenceType.IBAN, ACC2_ID, Currency.getInstance("EUR"), ACC2_RESOURCE_ID, ASPSP_ACC2_ID));
    }

    private ConsentTppInformation buildConsentTppInformation() {
        ConsentTppInformation tppInformation = new ConsentTppInformation();
        tppInformation.setTppInfo(buildTppInfo());
        tppInformation.setTppFrequencyPerDay(REQUESTED_FREQUENCY);
        tppInformation.setTppRedirectPreferred(true);
        return tppInformation;
    }

    private TppInfo buildTppInfo() {
        TppInfo info = new TppInfo();
        info.setAuthorisationNumber(AUTH_NR);
        info.setTppName(TPP_NAME);
        info.setTppRoles(Arrays.asList(TppRole.values()));
        info.setAuthorityId(AUTH_ID);
        info.setAuthorityName(AUTH_NAME);
        info.setCountry(COUNTRY);
        info.setOrganisation(ORGANISATION);
        info.setOrganisationUnit(ORGANISATION_UNIT);
        info.setCity(CITY);
        info.setState(STATE);
        //info.setTppRedirectUri(TPP_REDIRECT_URI);
        info.setCancelTppRedirectUri(TPP_CANCEL_URI);
        info.setIssuerCN(ISSUER_CN);
        return info;
    }

    private PsuIdData getPsuData() {
        return new PsuIdData(PSU_ID, PSU_ID_TYPE, CORPORATE_ID, CORPORATE_ID_TYPE, null);
    }
}
