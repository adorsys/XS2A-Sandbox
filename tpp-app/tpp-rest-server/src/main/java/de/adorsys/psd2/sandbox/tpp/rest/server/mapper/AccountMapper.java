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

import de.adorsys.ledgers.middleware.api.domain.account.AccountDetailsTO;
import de.adorsys.ledgers.middleware.api.domain.account.AccountReportTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccessTypeTO;
import de.adorsys.ledgers.middleware.api.domain.um.AccountAccessTO;
import de.adorsys.ledgers.middleware.api.domain.um.UserTO;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    AccountDetailsTO toAccountDetailsTO(DepositAccount depositAccount);

    @Mapping(target = "id", ignore = true)
    AccountAccessTO toAccountAccessTO(AccountAccess accountAccess);

    @Mapping(source = "usersAccessingAccount", target = "accesses")
    AccountReport toAccountReport(AccountReportTO report);

    List<UserAccess> toUserAccesses(List<UserTO> users);

    @Mapping(source = "user.login", target = "userLogin")
    @Mapping(expression = "java(user.getAccountAccesses().get(0).getScaWeight())", target = "scaWeight")
    @Mapping(expression = "java(toAccessType(user.getAccountAccesses().get(0).getAccessType()))", target = "accessType")
    UserAccess toUserAccess(UserTO user);

    AccessType toAccessType(AccessTypeTO accessTypeTO);
}
