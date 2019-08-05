package de.adorsys.psd2.sandbox.tpp.cms.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AisConsent {
    private PsuInfo psuInfo;
    private ThirdPartyInfo tppInfo;
    private Integer allowedFrequencyPerDay;
    private int requestedFrequencyPerDay;
    private AccountAccessInfo access;
    @JsonDeserialize(
        using = LocalDateDeserializer.class
    )
    @JsonSerialize(
        using = LocalDateSerializer.class
    )
    private LocalDate validUntil;
    private boolean recurringIndicator;
    private boolean tppRedirectPreferred;
    private boolean combinedServiceIndicator;
}
