package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.psd2.consent.api.pis.CmsPeriodicPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import lombok.Setter;

@Setter
public class ObaCmsPeriodicPayment extends CmsPeriodicPayment {
    private PaymentType paymentType; //TODO getPaymentType in CmsPeriodicPayment is FINAL so can't be overloaded

    public ObaCmsPeriodicPayment() {
        super(null);
    }

}
