import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {AccountDetailsTO, AuthorizeResponse} from '../../api/models';

import {ConsentAuthorizeResponse} from '../../api/models/consent-authorize-response';
import {PSUAISService} from '../../api/services';
import LoginUsingPOSTParams = PSUAISService.LoginUsingPOSTParams;
import AisAuthGetGETParams = PSUAISService.AisAuthUsingGETParams;
import RevokeConsentUsingDELETEParams = PSUAISService.RevokeConsentUsingDELETEParams;


@Injectable({
    providedIn: 'root'
})
export class AisService {

    constructor(private aisService: PSUAISService) {
    }

    public aisAuthCode(params: AisAuthGetGETParams): Observable<AuthorizeResponse> {
      return this.aisService.aisAuthUsingGET(params);
    }

    public aisAuthorise(params: LoginUsingPOSTParams): Observable<ConsentAuthorizeResponse> {
        return this.aisService.loginUsingPOST(params);
    }

    public getAccountsList(): Observable<Array<AccountDetailsTO>> {
        return this.aisService.getListOfAccountsUsingGET();
    }

    public startConsentAuth(params: PSUAISService.StartConsentAuthUsingPOSTParams): Observable<ConsentAuthorizeResponse> {
        return this.aisService.startConsentAuthUsingPOST(params);
    }

    public selectScaMethod(params: PSUAISService.SelectMethodUsingPOSTParams): Observable<ConsentAuthorizeResponse> {
        return this.aisService.selectMethodUsingPOST(params);
    }

    public authrizedConsent(params: PSUAISService.AuthrizedConsentUsingPOSTParams): Observable<ConsentAuthorizeResponse> {
        return this.aisService.authrizedConsentUsingPOST(params);
    }

    public revokeConsent(params: RevokeConsentUsingDELETEParams): Observable<ConsentAuthorizeResponse> {
      return this.aisService.revokeConsentUsingDELETE(params);
    }

    public aisDone(params: PSUAISService.AisDoneUsingGETParams): Observable<ConsentAuthorizeResponse> {
        return this.aisService.aisDoneUsingGET(params);
    }
}
