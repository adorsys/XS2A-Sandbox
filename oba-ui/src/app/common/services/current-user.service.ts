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

import { Injectable } from '@angular/core';
import { OnlineBankingAccountInformationService } from '../../api/services/online-banking-account-information.service';
import { UserTO } from '../../api/models/user-to';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';

@Injectable({
  providedIn: 'root',
})
export class CurrentUserService {
  constructor(
    private onlineBankingService: OnlineBankingAccountInformationService,
    private onlineBankingAuthorizationService: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService
  ) {}
  public getCurrentUser() {
    return this.onlineBankingService.getCurrentAccountInfo();
  }

  public updateUserDetails(updatedUserDetails: UserTO) {
    return this.onlineBankingAuthorizationService.updateUserDetailsUsingPUT(
      updatedUserDetails
    );
  }
}
