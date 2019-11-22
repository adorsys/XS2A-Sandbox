package de.adorsys.ledgers.oba.rest.server.domain;

import de.adorsys.psd2.consent.api.pis.CmsBulkPayment;
import de.adorsys.psd2.xs2a.core.profile.PaymentType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ObaCmsBulkPayment extends CmsBulkPayment {
    private PaymentType paymentType;
}
