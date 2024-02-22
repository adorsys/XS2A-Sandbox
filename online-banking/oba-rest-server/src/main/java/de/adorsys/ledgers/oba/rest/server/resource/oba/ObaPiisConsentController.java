/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

package de.adorsys.ledgers.oba.rest.server.resource.oba;

import de.adorsys.ledgers.oba.rest.api.resource.oba.ObaPiisConsentApi;
import de.adorsys.ledgers.oba.service.api.domain.ObaPiisConsent;
import de.adorsys.ledgers.oba.service.api.service.PiisConsentService;
import de.adorsys.ledgers.util.domain.CustomPageImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static de.adorsys.ledgers.oba.rest.api.resource.oba.ObaPiisConsentApi.BASE_PATH;


@Slf4j
@RestController
@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
public class ObaPiisConsentController implements ObaPiisConsentApi {

    private final PiisConsentService piisConsentService;

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<List<ObaPiisConsent>> getPiisConsents(String userLogin) {
        return ResponseEntity.ok(piisConsentService.getListOfConsents(userLogin));
    }

    @Override
    @PreAuthorize("#userLogin == authentication.principal.login")
    public ResponseEntity<CustomPageImpl<ObaPiisConsent>> getPiisConsentsPaged(String userLogin, int page, int size) {
        return ResponseEntity.ok(piisConsentService.getListOfConsentsPaged(userLogin, page, size));
    }

    @Override
    public ResponseEntity<Boolean> revokeConsent(String userLogin, String consentId) {
        return ResponseEntity.ok(piisConsentService.revokeConsent(userLogin, consentId));
    }
}
