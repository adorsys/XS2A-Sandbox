package de.adorsys.ledgers.oba.domain;

import java.util.List;

import de.adorsys.ledgers.domain.sca.SCAMethodTO;

public class AuthorizeResponse {

	private String scaId;
	
	private List<SCAMethodTO> scaMethods;
	
	private String scaOpId;
	
	private String userMessage;

	public String getScaId() {
		return scaId;
	}

	public void setScaId(String scaId) {
		this.scaId = scaId;
	}

	public List<SCAMethodTO> getScaMethods() {
		return scaMethods;
	}

	public void setScaMethods(List<SCAMethodTO> scaMethods) {
		this.scaMethods = scaMethods;
	}

	public String getScaOpId() {
		return scaOpId;
	}

	public void setScaOpId(String scaOpId) {
		this.scaOpId = scaOpId;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}
}
