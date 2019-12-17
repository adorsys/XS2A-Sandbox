package de.adorsys.ledgers.oba.service.api.service;

import de.adorsys.ledgers.oba.service.api.domain.ObaCmsPeriodicPayment;

import java.util.List;

public interface PaymentService {
    List<ObaCmsPeriodicPayment> getPeriodicPayments(String userLogin);

}
