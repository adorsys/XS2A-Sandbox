package de.adorsys.psd2.sandbox.tpp.cms.impl.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adorsys.psd2.consent.api.AccountInfo;
import de.adorsys.psd2.consent.api.ais.AisAccountAccessInfo;
import de.adorsys.psd2.consent.api.ais.CreateAisConsentRequest;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.*;
import de.adorsys.psd2.xs2a.core.ais.AccountAccessType;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import org.json.JSONException;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import org.skyscreamer.jsonassert.JSONAssert;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static de.adorsys.psd2.consent.api.AccountInfo.builder;
import static org.assertj.core.api.Assertions.assertThat;

public class AisConsentMapperTest {
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
    private static final TppRedirectUri TPP_REDIRECT_URI = new TppRedirectUri("http://adorsys.de", "http://google.com");
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
    private final AisConsentMapper mapper = Mappers.getMapper(AisConsentMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void toAccessInfoTest() {
        AisAccountAccessInfo expectedResult = getAisAccountAccess();
        AisAccountAccessInfo result = mapper.toAisAccountAccessInfo(getAccess());
        assertThat(result).isEqualToComparingFieldByFieldRecursively(expectedResult);
    }

    @Test
    public void toCmsAisConsentRequest() throws JsonProcessingException, JSONException {
        CreateAisConsentRequest expectedResult = getCmsAisConsentRequest();
        CreateAisConsentRequest result = mapper.toCmsAisConsentRequest(getAisConsent());
        String ex = objectMapper.writeValueAsString(expectedResult);
        String re = objectMapper.writeValueAsString(result);
        JSONAssert.assertEquals(re, ex, true); //TODO Could not make AssertJ properly compare results
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
        return consent;
    }

    private PsuInfo getPsuInfo() {
        PsuInfo psu = new PsuInfo();
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
        info.setTppRedirectUri(TP_REDIRECT_URI);
        info.setCancelTppRedirectUri(TP_CANCEL_URI);
        info.setIssuerCN(ISSUER_CN);
        return info;
    }

    private AccountAccessInfo getAccess() {
        AccountAccessInfo access = new AccountAccessInfo();
        access.setAccounts(getAccounts());
        access.setBalances(getAccounts());
        access.setTransactions(getAccounts());
        access.setAllPsd2(UserAccountAccessType.ALL_ACCOUNTS);
        access.setAvailableAccounts(UserAccountAccessType.ALL_ACCOUNTS);
        access.setAvailableAccountsWithBalance(UserAccountAccessType.ALL_ACCOUNTS);
        return access;
    }

    private List<UserAccountInfo> getAccounts() {
        UserAccountInfo[] arr = {
            UserAccountInfo.builder()
                .accountIdentifier(ACC1_ID)
                .accountType(UserAccountReferenceType.IBAN)
                .aspspAccountId(ASPSP_ACC1_ID)
                .currency(CURRENCY_STR)
                .resourceId(ACC1_RESOURCE_ID)
                .build(),
            UserAccountInfo.builder()
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
    private CreateAisConsentRequest getCmsAisConsentRequest() {
        CreateAisConsentRequest request = new CreateAisConsentRequest();
        request.setPsuData(getPsuData());
        request.setTppInfo(getCmsTppInfo());
        request.setAllowedFrequencyPerDay(ALLOWED_FREQUENCY);
        request.setRequestedFrequencyPerDay(REQUESTED_FREQUENCY);
        request.setAccess(getAisAccountAccess());
        request.setValidUntil(VALID_UNTIL);
        request.setRecurringIndicator(true);
        request.setTppRedirectPreferred(true);
        request.setCombinedServiceIndicator(true);
        return request;
    }

    private AisAccountAccessInfo getAisAccountAccess() {
        AisAccountAccessInfo access = new AisAccountAccessInfo();
        access.setAccounts(getAccountList());
        access.setBalances(getAccountList());
        access.setTransactions(getAccountList());
        access.setAvailableAccounts(AccountAccessType.ALL_ACCOUNTS);
        access.setAllPsd2(AccountAccessType.ALL_ACCOUNTS);
        access.setAvailableAccountsWithBalance(AccountAccessType.ALL_ACCOUNTS);

        return access;
    }

    private List<AccountInfo> getAccountList() {
        AccountInfo[] arr = {
            builder()
                .accountIdentifier(ACC1_ID)
                .accountReferenceType(AccountReferenceType.IBAN)
                .aspspAccountId(ASPSP_ACC1_ID)
                .currency(CURRENCY_STR)
                .resourceId(ACC1_RESOURCE_ID)
                .build(),
            builder()
                .accountIdentifier(ACC2_ID)
                .accountReferenceType(AccountReferenceType.IBAN)
                .aspspAccountId(ASPSP_ACC2_ID)
                .currency(CURRENCY_STR)
                .resourceId(ACC2_RESOURCE_ID)
                .build()
        };
        return Arrays.asList(arr);
    }

    private TppInfo getCmsTppInfo() {
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
