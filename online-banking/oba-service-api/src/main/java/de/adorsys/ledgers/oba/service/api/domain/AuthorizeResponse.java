/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.ledgers.oba.service.api.domain;

import java.util.List;

import de.adorsys.ledgers.middleware.api.domain.sca.ScaStatusTO;
import de.adorsys.ledgers.middleware.api.domain.um.ScaUserDataTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class AuthorizeResponse extends OnlineBankingResponse {
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

    /*
     * tpp redirect url
     */
    private String redirectUrl;
}
