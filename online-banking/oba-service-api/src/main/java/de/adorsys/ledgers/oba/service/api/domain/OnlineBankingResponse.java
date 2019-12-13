package de.adorsys.ledgers.oba.service.api.domain;

import java.util.ArrayList;
import java.util.List;

public class OnlineBankingResponse {

	private List<PsuMessage> psuMessages = new ArrayList<>();

	public List<PsuMessage> getPsuMessages() {
		return psuMessages;
	}

	public void setPsuMessages(List<PsuMessage> psuMessages) {
		this.psuMessages = psuMessages;
	}
}
