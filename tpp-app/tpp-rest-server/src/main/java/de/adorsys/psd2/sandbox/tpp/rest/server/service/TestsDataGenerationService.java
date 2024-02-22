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

package de.adorsys.psd2.sandbox.tpp.rest.server.service;

import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.ledgers.middleware.client.rest.UserMgmtRestClient;
import de.adorsys.psd2.sandbox.tpp.rest.server.exception.TppException;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.DataPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Currency;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TestsDataGenerationService {
    private static final String MSG_NO_BRANCH_SET = "This User does not belong to any Branch";
    private static final String CAN_NOT_LOAD_DEFAULT_DATA = "Can't load default data";

    private final ParseService parseService;
    private final RestExecutionService executionService;
    private final UserMgmtRestClient userMgmtRestClient;
    private final IbanGenerationService ibanGenerationService;

    public byte[] generate(boolean generatePayments, Currency currency) {
        Optional<UserTO> user = Optional.ofNullable(userMgmtRestClient.getUser().getBody());

        String branch = user.map(UserTO::getBranch)
                            .orElseThrow(() -> new TppException(MSG_NO_BRANCH_SET, 400));

        DataPayload payload = parseService.getDefaultData()
                                  .map(p -> p.updatePayload(ibanGenerationService::generateIbanForNisp, branch, currency, generatePayments))
                                  .orElseThrow(() -> new TppException(CAN_NOT_LOAD_DEFAULT_DATA, 400));

        executionService.updateLedgers(payload);
        return parseService.generateFileByPayload(payload);
    }
}
