package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CmsPisResponse {
    private PaymentTO payment;
    private String authorisationId;
    private String tppOkRedirectUri;
    private String tppNokRedirectUri;
}
