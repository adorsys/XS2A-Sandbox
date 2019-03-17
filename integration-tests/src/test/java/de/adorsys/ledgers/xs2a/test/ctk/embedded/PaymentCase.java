package de.adorsys.ledgers.xs2a.test.ctk.embedded;

import de.adorsys.psd2.model.PaymentInitiationSctJson;

public class PaymentCase {
	private String psuId;
	private PaymentInitiationSctJson payment;
	public String getPsuId() {
		return psuId;
	}
	public void setPsuId(String psuId) {
		this.psuId = psuId;
	}
	public PaymentInitiationSctJson getPayment() {
		return payment;
	}
	public void setPayment(PaymentInitiationSctJson payment) {
		this.payment = payment;
	}
}
