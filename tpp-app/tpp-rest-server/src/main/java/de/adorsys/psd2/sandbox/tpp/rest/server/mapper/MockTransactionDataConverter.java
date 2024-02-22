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

package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.MockBookingDetails;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MockTransactionDataConverter {

    List<MockBookingDetails> toLedgersMockTransactions(List<UserTransaction> transactions);

    @Mapping(source = "id", target = "userAccount")
    @Mapping(qualifiedByName = "toLocalDate", source = "postingDate", target = "bookingDate")
    @Mapping(qualifiedByName = "toLocalDate", source = "valueDate", target = "valueDate")
    @Mapping(source = "usage", target = "remittance")
    @Mapping(source = "payer", target = "crDrName")
    @Mapping(source = "accountNumber", target = "otherAccount")
    MockBookingDetails toLedgersMockTransaction(UserTransaction transaction);

    @Named("toLocalDate")
    default LocalDate toLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(date, formatter);
    }
}
