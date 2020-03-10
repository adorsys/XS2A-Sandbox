package de.adorsys.psd2.sandbox.tpp.cms.impl.mapper;

import de.adorsys.psd2.consent.api.ais.CmsConsent;
import de.adorsys.psd2.core.data.AccountAccess;
import de.adorsys.psd2.core.data.ais.AisConsentData;
import de.adorsys.psd2.core.mapper.ConsentDataMapper;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.*;
import de.adorsys.psd2.xs2a.core.ais.AccountAccessType;
import de.adorsys.psd2.xs2a.core.authorisation.AuthorisationTemplate;
import de.adorsys.psd2.xs2a.core.consent.ConsentStatus;
import de.adorsys.psd2.xs2a.core.consent.ConsentTppInformation;
import de.adorsys.psd2.xs2a.core.consent.ConsentType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import de.adorsys.psd2.xs2a.core.tpp.TppRole;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConsentMapper {
    private final ConsentDataMapper consentDataMapper;

    public CmsConsent mapToCmsConsent(AisConsent consent) {
        CmsConsent cmsConsent = new CmsConsent();
        AccountAccessInfo access = consent.getAccess();

        AisConsentData aisConsentData = new AisConsentData(toCmsAccessType(consent.getAvailableAccounts()), toCmsAccessType(consent.getAllPsd2()), toCmsAccessType(consent.getAvailableAccountsWithBalance()), false);
        byte[] aisConsentDataBytes = consentDataMapper.getBytesFromConsentData(aisConsentData);
        cmsConsent.setConsentData(aisConsentDataBytes);

        ConsentTppInformation tppInformation = new ConsentTppInformation();
        tppInformation.setTppInfo(toTppInfo(consent.getTppInfo()));
        tppInformation.setTppFrequencyPerDay(consent.getRequestedFrequencyPerDay());
        tppInformation.setTppRedirectPreferred(consent.isTppRedirectPreferred());
        cmsConsent.setTppInformation(tppInformation);

        AuthorisationTemplate authorisationTemplate = new AuthorisationTemplate();
        authorisationTemplate.setTppRedirectUri(toTppRedirectUti(consent.getTppInfo().getCancelTppRedirectUri()));
        cmsConsent.setAuthorisationTemplate(authorisationTemplate);

        cmsConsent.setFrequencyPerDay(consent.getAllowedFrequencyPerDay());
        cmsConsent.setInternalRequestId(UUID.randomUUID().toString());
        cmsConsent.setValidUntil(consent.getValidUntil());
        cmsConsent.setRecurringIndicator(consent.isRecurringIndicator());
        cmsConsent.setPsuIdDataList(Collections.singletonList(toPsuIdData(consent.getPsuInfo())));
        cmsConsent.setConsentType(ConsentType.AIS);
        cmsConsent.setTppAccountAccesses(toAccountAccess(access));
        cmsConsent.setAspspAccountAccesses(AccountAccess.EMPTY_ACCESS);
        cmsConsent.setConsentStatus(ConsentStatus.RECEIVED);
        return cmsConsent;
    }

    private AccountAccess toAccountAccess(AccountAccessInfo access) {
        List<AccountReference> accounts = toAccountReferences(access.getAccounts());
        List<AccountReference> balances = toAccountReferences(access.getBalances());
        List<AccountReference> transactions = toAccountReferences(access.getTransactions());
        return new AccountAccess(accounts, balances, transactions, null);
    }

    private List<AccountReference> toAccountReferences(List<AccountReferenceInfo> source) {
        if (CollectionUtils.isNotEmpty(source)) {
            return source.stream()
                       .map(r -> new AccountReference(
                           AccountReferenceType.valueOf(r.getAccountType().name()),
                           r.getAccountIdentifier(),
                           Currency.getInstance(r.getCurrency()), r.getResourceId(), r.getAspspAccountId()))
                       .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private PsuIdData toPsuIdData(PsuIdDataInfo psuInfo) {
        return new PsuIdData(psuInfo.getPsuId(), psuInfo.getPsuIdType(), psuInfo.getPsuCorporateId(), psuInfo.getPsuCorporateIdType(), null);
    }

    private AccountAccessType toCmsAccessType(UserAccountAccessType type) {
        return AccountAccessType.valueOf(type.name());
    }

    private TppInfo toTppInfo(ThirdPartyInfo source) {
        TppInfo tppInfo = new TppInfo();
        tppInfo.setAuthorisationNumber(source.getAuthorisationNumber());
        tppInfo.setTppName(source.getTppName());
        tppInfo.setTppRoles(toTppRoles(source.getTppRoles()));
        tppInfo.setAuthorityId(source.getAuthorityId());
        tppInfo.setAuthorityName(source.getAuthorityName());
        tppInfo.setCountry(source.getCountry());
        tppInfo.setOrganisation(source.getOrganisation());
        tppInfo.setOrganisationUnit(source.getOrganisationUnit());
        tppInfo.setCity(source.getCity());
        tppInfo.setState(source.getState());
        tppInfo.setIssuerCN(source.getIssuerCN());
        tppInfo.setCancelTppRedirectUri(toTppRedirectUti(source.getCancelTppRedirectUri()));
        return tppInfo;
    }

    private List<TppRole> toTppRoles(List<ThirdPartyRole> tppRoles) {
        if (CollectionUtils.isNotEmpty(tppRoles)) {
            return tppRoles.stream()
                       .map(r -> TppRole.valueOf(r.name()))
                       .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private TppRedirectUri toTppRedirectUti(ThirdPartyRedirectUri redirectUri) {
        return Optional.ofNullable(redirectUri)
                   .map(u -> new TppRedirectUri(u.getUri(), u.getNokUri()))
                   .orElseGet(() -> new TppRedirectUri("", null));
    }

}
