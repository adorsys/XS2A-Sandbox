import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models';
import { AuthorizeResponse } from '../../api/models/authorize-response';
import { PSUPISService } from '../../api/services';

import PisAuthUsingGETParams = PSUPISService.PisAuthUsingGETParams;
import LoginUsingPOST3Params = PSUPISService.LoginUsingPOST3Params;
import SelectMethodUsingPOST2Params = PSUPISService.SelectMethodUsingPOST2Params;
import AuthorisePaymentUsingPOSTParams = PSUPISService.AuthrizedPaymentUsingPOSTParams;
import {HttpResponse} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class PisService {

  constructor(private pisService: PSUPISService) {}

  public pisAuthCode(params: PisAuthUsingGETParams): Observable<HttpResponse<AuthorizeResponse>> {
    return this.pisService.pisAuthUsingGETResponse(params);
  }

  public pisLogin(params: LoginUsingPOST3Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.loginUsingPOST3(params);
  }

  public selectScaMethod(params: SelectMethodUsingPOST2Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.selectMethodUsingPOST2(params);
  }

  public authorizePayment(params: AuthorisePaymentUsingPOSTParams): Observable<PaymentAuthorizeResponse> {
    return this.pisService.authrizedPaymentUsingPOST(params);
  }

  public pisDone(params: PSUPISService.PisDoneUsingGET1Params): Observable<PaymentAuthorizeResponse> {
    return this.pisService.pisDoneUsingGET1(params);
  }
}
