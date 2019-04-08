import {Injectable} from '@angular/core';
import {PSUPISService} from "../../api/services";
import {Observable} from "rxjs";
import {AuthorizeResponse} from "../../api/models/authorize-response";
import PisAuthUsingGETParams = PSUPISService.PisAuthUsingGETParams;
import {PaymentAuthorizeResponse} from "../../api/models";
import LoginUsingPOST1Params = PSUPISService.LoginUsingPOST1Params;
import SelectMethodUsingPOST1Params = PSUPISService.SelectMethodUsingPOST1Params;

@Injectable({
  providedIn: 'root'
})
export class PisService {

  constructor(private pisService: PSUPISService) {
  }

  public pisAuthCode(params: PisAuthUsingGETParams): Observable<AuthorizeResponse> {
    return this.pisService.pisAuthUsingGET(params);
  }

  public pisLogin(params: LoginUsingPOST1Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.loginUsingPOST1(params);
  }

  public selectScaMethod(params: SelectMethodUsingPOST1Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.selectMethodUsingPOST1(params);
  }
}
