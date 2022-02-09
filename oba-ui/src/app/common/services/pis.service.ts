/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

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
