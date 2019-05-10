import {Injectable} from '@angular/core';
import {PSUPISCancellationService, PSUPISService} from "../../api/services";
import {Observable} from "rxjs";
import {AuthorizeResponse} from "../../api/models/authorize-response";
import {PaymentAuthorizeResponse} from "../../api/models";
import PisAuthUsingGETParams = PSUPISService.PisAuthUsingGETParams;
import LoginUsingPOST2Params = PSUPISService.LoginUsingPOST2Params;
import SelectMethodUsingPOST2Params = PSUPISService.SelectMethodUsingPOST2Params;
import AuthorisePaymentUsingPOSTParams = PSUPISCancellationService.AuthorisePaymentUsingPOSTParams;

@Injectable({
  providedIn: 'root'
})
export class PisService {

  constructor(private pisService: PSUPISService) {
  }

  public pisAuthCode(params: PisAuthUsingGETParams): Observable<AuthorizeResponse> {
    return this.pisService.pisAuthUsingGET(params);
  }

  public pisLogin(params: LoginUsingPOST2Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.loginUsingPOST2(params);
  }

  public selectScaMethod(params: SelectMethodUsingPOST2Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.selectMethodUsingPOST2(params);
  }

  public authorizePayment(params: AuthorisePaymentUsingPOSTParams): Observable<PaymentAuthorizeResponse> {
    return this.pisService.authrizedPaymentUsingPOST(params);
  }
}
