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

import { HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { AccountDetailsTO, AuthorizeResponse } from '../../api/models';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService } from '../../api/services';

import LoginUsingPOSTParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.LoginUsingPOSTParams;
import AisAuthGetGETParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AisAuthUsingGETParams;
import RevokeConsentUsingDELETEParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.RevokeConsentUsingDELETEParams;
@Injectable({
  providedIn: 'root',
})
export class AisService {
  constructor(
    private aisService: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService
  ) {}

  public aisAuthCode(
    params: AisAuthGetGETParams
  ): Observable<HttpResponse<AuthorizeResponse>> {
    return this.aisService.aisAuthUsingGETResponse(params);
  }

  public aisAuthorise(
    params: LoginUsingPOSTParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.loginUsingPOST(params);
  }

  public getAccountsList(): Observable<Array<AccountDetailsTO>> {
    return this.aisService.getListOfAccountsUsingGET();
  }

  public startConsentAuth(
    params: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.StartConsentAuthUsingPOSTParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.startConsentAuthUsingPOST(params);
  }

  public selectScaMethod(
    params: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.SelectMethodUsingPOSTParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.selectMethodUsingPOST(params);
  }

  public authrizedConsent(
    params: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AuthrizedConsentUsingPOSTParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.authrizedConsentUsingPOST(params);
  }

  public revokeConsent(
    params: RevokeConsentUsingDELETEParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.revokeConsentUsingDELETE(params);
  }

  public aisDone(
    params: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AisDoneUsingGETParams
  ): Observable<ConsentAuthorizeResponse> {
    return this.aisService.aisDoneUsingGET(params);
  }
}
