package de.adorsys.ledgers.oba.rest.server.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.psd2.consent.api.ais.AisAccountAccess;
import de.adorsys.psd2.consent.api.ais.AisAccountConsent;
import de.adorsys.psd2.xs2a.core.consent.AisConsentRequestType;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;

@Component
public class AisConsentMapper {
	
	public AisConsentTO toTo(AisAccountConsent consent) {
		AisConsentTO a = new AisConsentTO();
		a.setId(consent.getId());
		a.setUserId(Optional.ofNullable(consent.getPsuData()).map(p -> p.getPsuId()).orElse(null));
		a.setTppId(Optional.ofNullable(consent.getTppInfo()).map(t -> t.getAuthorisationNumber()).orElse(null));
		a.setFrequencyPerDay(consent.getFrequencyPerDay());
		a.setAccess(toAccess(consent));
		a.setValidUntil(consent.getValidUntil());
		a.setRecurringIndicator(consent.isRecurringIndicator());
		return a;
	}

	private AisAccountAccessInfoTO toAccess(AisAccountConsent consent) {
		if(consent.getAccess()==null) {
			return null;
		}
		AisAccountAccess access = consent.getAccess();
		AisAccountAccessInfoTO a = new AisAccountAccessInfoTO();
		a.setAccounts(Optional.ofNullable(access.getAccounts()).map(l -> mapSpiAccountReferencesToString(l)).orElse(null));
		a.setBalances(Optional.ofNullable(access.getBalances()).map(l -> mapSpiAccountReferencesToString(l)).orElse(null));
		a.setTransactions(Optional.ofNullable(access.getTransactions()).map(l -> mapSpiAccountReferencesToString(l)).orElse(null));
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

	protected String mapSpiAccountReferenceToString(AccountReference s)
	{
		return s.getIban();
	}
	protected List<String> mapSpiAccountReferencesToString(List<AccountReference> s)
	{
		return s.stream().map(this::mapSpiAccountReferenceToString).collect(Collectors.toList());
	}
}
