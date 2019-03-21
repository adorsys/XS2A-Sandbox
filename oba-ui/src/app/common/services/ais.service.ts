import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/internal/Observable';

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

}
