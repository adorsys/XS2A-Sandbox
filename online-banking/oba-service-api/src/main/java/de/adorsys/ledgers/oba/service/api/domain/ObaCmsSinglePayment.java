package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.psd2.consent.api.pis.CmsSinglePayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import lombok.Data;

@Data
public class ObaCmsSinglePayment extends CmsSinglePayment {
    private PaymentType paymentType;

    public ObaCmsSinglePayment() {
        super(null);
    }

}
