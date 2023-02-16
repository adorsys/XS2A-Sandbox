/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
