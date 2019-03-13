package de.adorsys.ledgers.oba.rest.server.mapper;

import java.util.Currency;

import org.springframework.stereotype.Component;

import de.adorsys.ledgers.middleware.api.domain.payment.AmountTO;
import de.adorsys.psd2.consent.api.pis.CmsAmount;

@Component
public class CurrencyMapper {
    public Currency toCurrency(String currency) {
        return currency == null
                       ? null
                       : Currency.getInstance(currency);
    }
    
    public String currencyToString(Currency currency) {
        return currency == null
                       ? null
                       : currency.getCurrencyCode();
    }
    
    @SuppressWarnings("PMD.ShortMethodName")
    public CmsAmount map(AmountTO s) {
    	return new CmsAmount(s.getCurrency(), s.getAmount());
    }
}
