package de.adorsys.ledgers.oba.domain;

import org.springframework.http.HttpStatus;

import de.adorsys.ledgers.oba.consentref.ConsentReference;
import de.adorsys.psd2.consent.api.pis.CmsPaymentResponse;

public class PaymentWorkflow {
	private final CmsPaymentResponse paymentResponse;

	private String paymentStatus;
	private String authCodeMessage;
	private HttpStatus errorCode;
	private PaymentAuthorizeResponse authResponse;
	private ConsentReference consentReference;
	
	public PaymentWorkflow(CmsPaymentResponse paymentResponse) {
		super();
		this.paymentResponse = paymentResponse;
	}

	public CmsPaymentResponse getPaymentResponse() {
		return paymentResponse;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getAuthCodeMessage() {
		return authCodeMessage;
	}

	public void setAuthCodeMessage(String authCodeMessage) {
		this.authCodeMessage = authCodeMessage;
	}

	public HttpStatus getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(HttpStatus errorCode) {
		this.errorCode = errorCode;
	}

	public PaymentAuthorizeResponse getAuthResponse() {
		return authResponse;
	}

	public void setAuthResponse(PaymentAuthorizeResponse authResponse) {
		this.authResponse = authResponse;
	}

	public ConsentReference getConsentReference() {
		return consentReference;
	}

	public void setConsentReference(ConsentReference consentReference) {
		this.consentReference = consentReference;
	}
}