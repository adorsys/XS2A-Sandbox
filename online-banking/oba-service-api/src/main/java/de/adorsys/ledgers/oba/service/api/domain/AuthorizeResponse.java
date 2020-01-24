package de.adorsys.ledgers.oba.service.api.domain;

import java.util.List;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class AuthorizeResponse extends OnlineBankingResponse  {
	/*
	 * The id of the business process, login, payment, consent.
	 */
	private String encryptedConsentId;

	private List<ScaUserDataTO> scaMethods;

	/*
	 * The id of this authorisation instance.
	 */
	private String authorisationId;

	/*
	 * The sca status is used to manage authorisation flows.
	 */
	private ScaStatusTO scaStatus;

    /*
     * Auth confirmation code in case of SCA is UNCONFIRMED
     */
	private String authConfirmationCode;
}
