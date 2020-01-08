package de.adorsys.psd2.sandbox.tpp.cms.impl.mapper;

import de.adorsys.psd2.consent.api.AccountInfo;
import de.adorsys.psd2.consent.api.ais.AisAccountAccessInfo;
import de.adorsys.psd2.consent.api.ais.CreateAisConsentRequest;
import de.adorsys.psd2.sandbox.tpp.cms.api.domain.*;
import de.adorsys.psd2.xs2a.core.profile.AccountReferenceType;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppRedirectUri;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AisConsentMapper {
    @Mapping(target = "psuData", source = "psuInfo")
    CreateAisConsentRequest toCmsAisConsentRequest(AisConsent aisConsent);

    AisAccountAccessInfo toAisAccountAccessInfo(AccountAccessInfo info);

    default AccountInfo toAccountInfo(UserAccountInfo userAccountInfo) {
        return Optional.ofNullable(userAccountInfo)
                   .map(i -> AccountInfo.builder()
                                 .resourceId(i.getResourceId())
                                 .aspspAccountId(i.getAspspAccountId())
                                 .accountIdentifier(i.getAccountIdentifier())
                                 .accountReferenceType(AccountReferenceType.valueOf(i.getAccountType().name()))
                                 .currency(i.getCurrency())
                                 .build())
                   .orElseGet(() -> AccountInfo.builder().build());
    }

    default PsuIdData toPsuIdData(PsuInfo psuInfo) {
        return Optional.ofNullable(psuInfo)
                   .map(p -> new PsuIdData(p.getPsuId(), p.getPsuIdType(), p.getPsuCorporateId(), p.getPsuCorporateIdType(), null))
                   .orElseGet(PsuIdData::new);
    }

    default TppRedirectUri toTppRedirectUti(ThirdPartyRedirectUri redirectUri) {
        return Optional.ofNullable(redirectUri)
                   .map(u -> new TppRedirectUri(u.getUri(), u.getNokUri()))
                   .orElseGet(() -> new TppRedirectUri("", null));
    }
}
