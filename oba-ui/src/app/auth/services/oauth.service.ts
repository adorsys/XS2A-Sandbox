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

import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { OnlineBankingOauthAuthorizationService } from '../../api/services/online-banking-oauth-authorization.service';
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;

@Injectable({
  providedIn: 'root',
})
export class OauthService {
  constructor(
    private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService
  ) {}

  public authorize(params: OauthCodeUsingPOSTParams): Observable<any> {
    return this.onlineBankingOauthAuthorizationService.oauthCodeUsingPOST(
      params
    );
  }
}
