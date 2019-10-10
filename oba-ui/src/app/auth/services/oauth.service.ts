import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {OnlineBankingOauthAuthorizationService} from "../../api/services/online-banking-oauth-authorization.service";
import OauthCodeUsingPOSTParams = OnlineBankingOauthAuthorizationService.OauthCodeUsingPOSTParams;

@Injectable({
  providedIn: 'root'
})
export class OauthService {

  constructor(
    private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService
  ) { }

  public authorize(params: OauthCodeUsingPOSTParams): Observable<any> {
    return this.onlineBankingOauthAuthorizationService.oauthCodeUsingPOST(params);
  }
}
