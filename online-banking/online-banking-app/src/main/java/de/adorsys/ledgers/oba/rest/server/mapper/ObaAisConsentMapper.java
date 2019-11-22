package de.adorsys.ledgers.oba.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.CmsAisAccountConsent;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ObaAisConsentMapper {

    public AisConsentTO toTo(CmsAisAccountConsent consent) {
        return new AisConsentTO(consent.getId(),
            resolveUserId(consent.getPsuIdDataList()),
            Optional.ofNullable(consent.getTppInfo()).map(TppInfo::getAuthorisationNumber).orElse(null),
            consent.getFrequencyPerDay(),
            toAccess(consent.getAccess()),
            consent.getValidUntil(),
            consent.isRecurringIndicator());
    }

    private AisAccountAccessInfoTO toAccess(AisAccountAccess access) {
        return new AisAccountAccessInfoTO(
            getIbansFromAccountReference(access.getAccounts()),
            getIbansFromAccountReference(access.getBalances()),
            getIbansFromAccountReference(access.getTransactions()),
            mapAccessType(access.getAvailableAccounts()),
            mapAccessType(access.getAllPsd2())
        );
    }

    private AisAccountAccessTypeTO mapAccessType(String accessType) {
        return Optional.ofNullable(accessType).map(AisAccountAccessTypeTO::valueOf).orElse(null);
    }

    public AisAccountAccess accountAccess(AisAccountAccessInfoTO access, List<AccountDetailsTO> accountDetails) {
        // TODO missing relationship to currency here.
        List<AccountReference> accounts = mapToAccountReference(access.getAccounts(), accountDetails);
        List<AccountReference> balances = mapToAccountReference(access.getBalances(), accountDetails);
        List<AccountReference> transactions = mapToAccountReference(access.getTransactions(), accountDetails);
        String availableAccounts = Optional.ofNullable(access.getAvailableAccounts())
                                       .map(Enum::name)
                                       .orElse(null);

        String allPsd2 = Optional.ofNullable(access.getAllPsd2())
                             .map(Enum::name)
                             .orElse(null);

        return new AisAccountAccess(accounts, balances, transactions, availableAccounts, allPsd2, null, null);
    }

    private List<AccountReference> mapToAccountReference(List<String> ibans, List<AccountDetailsTO> accountDetails) {
        return ibans == null
                   ? null
                   : ibans.stream().map(iban -> toAccountReference(iban, accountDetails))
                         .collect(Collectors.toList());

    }

    private AccountReference toAccountReference(String iban, List<AccountDetailsTO> accountDetails) {
        return accountDetails.stream().filter(a -> a.getIban().equals(iban)).findFirst()
                   .map(this::accountDetail2Reference).orElse(null);

    }

    private AccountReference accountDetail2Reference(AccountDetailsTO ad) {
        AccountReference a = new AccountReference();
        a.setAspspAccountId(ad.getId());
        a.setBban(ad.getBban());
        a.setCurrency(ad.getCurrency());
        a.setIban(ad.getIban());
        a.setMaskedPan(ad.getMaskedPan());
        a.setMsisdn(ad.getMsisdn());
        a.setPan(ad.getPan());
        return a;
    }

    private String resolveUserId(List<PsuIdData> psuIdDataList) {
        if (CollectionUtils.isEmpty(psuIdDataList)) {
            return null;
        }
        return getFirstPsu(psuIdDataList)
                   .getPsuId();
    }

    private PsuIdData getFirstPsu(List<PsuIdData> psuIdDataList) {
        return psuIdDataList.get(0);
    }

    private List<String> getIbansFromAccountReference(List<AccountReference> references) {
        return Optional.ofNullable(references)
                   .map(this::mapAccountReferencesToString)
                   .orElse(null);
    }

    private List<String> mapAccountReferencesToString(List<AccountReference> references) {
        return references.stream()
                   .map(AccountReference::getIban)
                   .collect(Collectors.toList());
    }
}
