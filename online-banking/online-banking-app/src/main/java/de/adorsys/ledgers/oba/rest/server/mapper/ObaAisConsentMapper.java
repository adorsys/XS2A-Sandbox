package de.adorsys.ledgers.oba.rest.server.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import de.adorsys.psd2.xs2a.core.psu.PsuIdData;
import de.adorsys.psd2.xs2a.core.tpp.TppInfo;

@Component
public class ObaAisConsentMapper {

	public AisConsentTO toTo(AisAccountConsent consent) {
		AisConsentTO a = new AisConsentTO();
		a.setId(consent.getId());
		a.setUserId(resolveUserId(consent.getPsuIdDataList())); // TODO should be changed after implementation of multiple sca https://git.adorsys.de/adorsys/xs2a/ledgers/issues/205
		a.setTppId(Optional.ofNullable(consent.getTppInfo()).map(TppInfo::getAuthorisationNumber).orElse(null));
		a.setFrequencyPerDay(consent.getFrequencyPerDay());
		a.setAccess(toAccess(consent));
		a.setValidUntil(consent.getValidUntil());
		a.setRecurringIndicator(consent.isRecurringIndicator());
		return a;
	}

	public AisAccountAccess accountAccess(AisAccountAccessInfoTO access, List<AccountDetailsTO> accountDetails) {
		// TODO missing relationship to currency here.
		List<AccountReference> accounts = mapToAccountReference(access.getAccounts(), accountDetails);
		List<AccountReference> balances = mapToAccountReference(access.getBalances(), accountDetails);
		List<AccountReference> transactions = mapToAccountReference(access.getTransactions(), accountDetails);
		String availableAccounts = access.getAvailableAccounts()!=null
				? access.getAvailableAccounts().name() : null;
		String allPsd2 = access.getAllPsd2()!=null
				? access.getAllPsd2().name() : null;
        return new AisAccountAccess(accounts, balances, transactions, availableAccounts, allPsd2, null);
	}

	private List<AccountReference> mapToAccountReference(List<String> ibans, List<AccountDetailsTO> accountDetails){
		return ibans==null
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
    if(CollectionUtils.isEmpty(psuIdDataList)) {
      return null;
    }
    return getFirstPsu(psuIdDataList)
             .getPsuId();
  }

  private PsuIdData getFirstPsu(List<PsuIdData> psuIdDataList) {
    return psuIdDataList.get(0);
  }


  private AisAccountAccessInfoTO toAccess(AisAccountConsent consent) {
		if(consent.getAccess()==null) {
			return null;
		}
		AisAccountAccess access = consent.getAccess();
		AisAccountAccessInfoTO a = new AisAccountAccessInfoTO();
    a.setAccounts(getIbansFromAccountReference(access.getAccounts()));
    a.setBalances(getIbansFromAccountReference(access.getBalances()));
    a.setTransactions(getIbansFromAccountReference(access.getTransactions()));
    AisConsentRequestType aisConsentRequestType = consent.getAisConsentRequestType();
		boolean withBalance = consent.isWithBalance();
		if(aisConsentRequestType!=null) {
			switch (aisConsentRequestType) {
			case ALL_AVAILABLE_ACCOUNTS:
				if(withBalance) {
					a.setAvailableAccounts(AisAccountAccessTypeTO.ALL_ACCOUNTS_WITH_BALANCES);
				} else {
					a.setAvailableAccounts(AisAccountAccessTypeTO.ALL_ACCOUNTS);
				}
				break;
			default:
				if(withBalance) {
					a.setAllPsd2(AisAccountAccessTypeTO.ALL_ACCOUNTS_WITH_BALANCES);
				} else {
					a.setAllPsd2(AisAccountAccessTypeTO.ALL_ACCOUNTS);
				}
				break;
			}

		}
		return a;
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
