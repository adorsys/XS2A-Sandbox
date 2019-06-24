package de.adorsys.ledgers.xs2a.test.ctk.redirect;

import de.adorsys.psd2.model.PaymentInitiationJson;

public class PaymentCase {
	private String psuId;
	private PaymentInitiationJson payment;
	public String getPsuId() {
		return psuId;
	}
	public void setPsuId(String psuId) {
		this.psuId = psuId;
	}
	public PaymentInitiationJson getPayment() {
		return payment;
	}
	public void setPayment(PaymentInitiationJson payment) {
		this.payment = payment;
	}
}
