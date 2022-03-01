/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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

package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.um.AisAccountAccessInfoTO;
import de.adorsys.ledgers.middleware.api.domain.um.AisConsentTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.consent.api.piis.v1.CmsPiisConsent;
import de.adorsys.psd2.consent.aspsp.api.piis.CreatePiisConsentRequest;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.AccountAccess;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.PiisConsent;
import de.adorsys.psd2.xs2a.core.profile.AccountReference;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class TppPiisConsentMapper {

    public CreatePiisConsentRequest toPiisConsentRequest(PiisConsent piisConsent) {
        CreatePiisConsentRequest request = new CreatePiisConsentRequest();

        request.setTppAuthorisationNumber(piisConsent.getTppAuthorisationNumber());
        request.setValidUntil(piisConsent.getValidUntil());
        AccountReference account = new AccountReference();
        account.setIban(piisConsent.getAccess().getIban());
        account.setCurrency(piisConsent.getAccess().getCurrency());
        request.setAccount(account);

        return request;
    }

    public AisConsentTO toAisConsentTO(PiisConsent piisConsent, UserTO user) {
        AisConsentTO ledgersPiisConsent = new AisConsentTO();

        AisAccountAccessInfoTO access = new AisAccountAccessInfoTO();
        access.setAccounts(Collections.singletonList(piisConsent.getAccess().getIban()));
        ledgersPiisConsent.setAccess(access);
        ledgersPiisConsent.setTppId(piisConsent.getTppAuthorisationNumber());
        ledgersPiisConsent.setUserId(user.getId());
        ledgersPiisConsent.setValidUntil(piisConsent.getValidUntil());

        return ledgersPiisConsent;
    }

    public PiisConsent toPiisConsent(CmsPiisConsent piisConsent) {
        PiisConsent tppPiisConsent = new PiisConsent();

        AccountAccess access = new AccountAccess();
        access.setIban(piisConsent.getAccount().getIban());
        access.setCurrency(piisConsent.getAccount().getCurrency());
        tppPiisConsent.setAccess(access);
        tppPiisConsent.setConsentId(piisConsent.getId());
        tppPiisConsent.setTppAuthorisationNumber(piisConsent.getTppAuthorisationNumber());
        tppPiisConsent.setValidUntil(piisConsent.getExpireDate());

        return tppPiisConsent;
    }


}
