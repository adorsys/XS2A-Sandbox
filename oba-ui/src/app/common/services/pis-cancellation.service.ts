import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services';

import LoginUsingPOST2Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params;
import SelectMethodUsingPOST1Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params;
import AuthorisePaymentUsingPOSTParams = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams;

@Injectable({
  providedIn: 'root',
})
export class PisCancellationService {
  constructor(
    private pisCancellationService: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService
  ) {}

  public pisCancellationLogin(
    params: LoginUsingPOST2Params
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.loginUsingPOST2(params);
  }

  public selectScaMethod(
    params: SelectMethodUsingPOST1Params
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.selectMethodUsingPOST1(params);
  }

  public authorizePayment(
    params: AuthorisePaymentUsingPOSTParams
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.authorisePaymentUsingPOST(params);
  }

  public pisCancellationDone(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisCancellationService.pisDoneUsingGET(params);
  }
}
