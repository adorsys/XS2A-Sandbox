import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models';
import { AuthorizeResponse } from '../../api/models/authorize-response';

import PisAuthUsingGETParams = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams;
import LoginUsingPOST3Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params;
import SelectMethodUsingPOST2Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params;
import AuthorisePaymentUsingPOSTParams = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams;
import { HttpResponse } from '@angular/common/http';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';

@Injectable({
  providedIn: 'root',
})
export class PisService {
  constructor(
    private pisService: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService
  ) {}

  public pisAuthCode(
    params: PisAuthUsingGETParams
  ): Observable<HttpResponse<AuthorizeResponse>> {
    return this.pisService.pisAuthUsingGETResponse(params);
  }

  public pisLogin(
    params: LoginUsingPOST3Params
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisService.loginUsingPOST3(params);
  }

  public selectScaMethod(
    params: SelectMethodUsingPOST2Params
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisService.selectMethodUsingPOST2(params);
  }

  public authorizePayment(
    params: AuthorisePaymentUsingPOSTParams
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisService.authrizedPaymentUsingPOST(params);
  }

  public pisDone(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params
  ): Observable<PaymentAuthorizeResponse> {
    return this.pisService.pisDoneUsingGET1(params);
  }
}
