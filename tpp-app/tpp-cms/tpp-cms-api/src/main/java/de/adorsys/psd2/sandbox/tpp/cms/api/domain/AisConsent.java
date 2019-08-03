package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AisConsent {
    private PsuInfo psuInfo;
    private ThirdPartyInfo tppInfo;
    private Integer allowedFrequencyPerDay;
    private int requestedFrequencyPerDay;
    private AccountAccessInfo access;
    private LocalDate validUntil;
    private boolean recurringIndicator;
    private boolean tppRedirectPreferred;
    private boolean combinedServiceIndicator;
}
