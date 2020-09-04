package de.adorsys.ledgers.oba.service.api.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OnlineBankingResponse {

    private List<PsuMessage> psuMessages = new ArrayList<>();

}
