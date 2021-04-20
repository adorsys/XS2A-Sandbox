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
