import {Injectable} from '@angular/core';
import {PSUPISCancellationService} from "../../api/services";
import {Observable} from "rxjs";
import {PaymentAuthorizeResponse} from "../../api/models/payment-authorize-response";
import LoginUsingPOST1Params = PSUPISCancellationService.LoginUsingPOST1Params;
import SelectMethodUsingPOST1Params = PSUPISCancellationService.SelectMethodUsingPOST1Params;
import AuthorisePaymentUsingPOSTParams = PSUPISCancellationService.AuthorisePaymentUsingPOSTParams;

@Injectable({
  providedIn: 'root'
})
export class PisCancellationService {

  constructor(private pisCancellationService: PSUPISCancellationService) {
  }

  public pisCancellationLogin(params: LoginUsingPOST1Params): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.loginUsingPOST1(params);
  }

  public selectScaMethod(params: SelectMethodUsingPOST1Params): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.selectMethodUsingPOST1(params);
  }

  public authorizePayment(params: AuthorisePaymentUsingPOSTParams): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.authorisePaymentUsingPOST(params);
  }
}
