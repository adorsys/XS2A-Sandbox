package de.adorsys.ledgers.oba.service.api.domain;

import de.adorsys.ledgers.middleware.api.domain.payment.PaymentTO;
import lombok.Data;

@Data
public class PaymentAuthorizeResponse extends AuthorizeResponse {
    private final PaymentTO payment;
    private String authMessageTemplate;
}
