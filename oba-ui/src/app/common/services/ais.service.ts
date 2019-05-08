import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/internal/Observable';
import {AccountDetailsTO} from '../../../../api/models/account-details-to';

import {ConsentAuthorizeResponse} from '../../../../api/models/consent-authorize-response';
import {PSUAISService} from '../../../../api/services/psuais.service';
import LoginUsingPOSTParams = PSUAISService.LoginUsingPOSTParams;


@Injectable({
    providedIn: 'root'
})
export class AisService {

    constructor(private aisService: PSUAISService) {
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

    public aisDone(params: PSUAISService.AisDoneUsingGETParams): void {
        this.aisService.aisDoneUsingGET(params);
    }
}
