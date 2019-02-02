package de.adorsys.ledgers.oba.rest.api.domain;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

import de.adorsys.ledgers.middleware.api.domain.sca.SCAResponseTO;
import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.BearerTokenTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import de.adorsys.ledgers.oba.rest.api.consentref.ConsentReference;
import de.adorsys.psd2.consent.psu.api.ais.CmsAisConsentResponse;

public class ConsentWorkflow {
	private final CmsAisConsentResponse consentResponse;

	private String consentStatus;
	private String authCodeMessage;
	private HttpStatus errorCode;
	private ConsentAuthorizeResponse authResponse;
	private final ConsentReference consentReference;
	private SCAResponseTO scaResponse;
	
	public ConsentWorkflow(@NotNull CmsAisConsentResponse consentResponse, ConsentReference consentReference) {
		if(consentResponse==null || consentReference==null) {
			throw new IllegalStateException("Do not allow null input.");
		}
		this.consentResponse = consentResponse;
		this.consentReference = consentReference;
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

	public ConsentAuthorizeResponse getAuthResponse() {
		return authResponse;
	}

	public void setAuthResponse(ConsentAuthorizeResponse authResponse) {
		this.authResponse = authResponse;
	}

	public ConsentReference getConsentReference() {
		return consentReference;
	}

	public String getConsentStatus() {
		return consentStatus;
	}

	public void setConsentStatus(String consentStatus) {
		this.consentStatus = consentStatus;
	}

	public CmsAisConsentResponse getConsentResponse() {
		return consentResponse;
	}

	public String consentId() {
		return consentResponse.getAccountConsent().getId();
	}
	public String authId() {
		return consentResponse.getAuthorisationId();
	}
	
	public String encryptedConsentId() {
		return consentReference.getEncryptedConsentId();
	}

	public SCAResponseTO getScaResponse() {
		return scaResponse;
	}

	public void setScaResponse(SCAResponseTO scaResponse) {
		this.scaResponse = scaResponse;
	}

	public BearerTokenTO bearerToken() {
		return scaResponse==null
				? null
						: scaResponse.getBearerToken();
	}

	public boolean singleScaMethod() {
		return scaResponse.getScaMethods()!=null && scaResponse.getScaMethods().size()==1;
	}

	public List<ScaUserDataTO> scaMethods() {
		return scaResponse.getScaMethods();
	}

	public ScaStatusTO scaStatus() {
		return scaResponse.getScaStatus();
	}
}