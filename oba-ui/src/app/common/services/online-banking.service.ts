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

import { AccountDetailsTO } from '../../api/models/account-details-to';
import { OnlineBankingAccountInformationService } from '../../api/services/online-banking-account-information.service';
import { OnlineBankingConsentsService } from '../../api/services/online-banking-consents.service';
import { AuthService } from './auth.service';
import { CustomPageImplTransactionTO } from '../../api/models/custom-page-impl-transaction-to';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';
import { CustomPageImplObaAisConsent } from '../../api/models/custom-page-impl-ais-consents';
import { CustomPageImplPaymentTO } from '../../api/models/custom-page-impl-paayment-to';
import { SCAPaymentResponseTO } from '../../api/models/scapayment-response-to';
import { OnlineBankingPaymentCancService } from '../../api/services/online-banking-payment-canc.service';
import { map as __map } from 'rxjs/internal/operators/map';
import { AuthorizeResponse } from '../../api/models/authorize-response';
import { HttpResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class OnlineBankingService {
  constructor(
    private authService: AuthService,
    private onlineBankingAccountInfoService: OnlineBankingAccountInformationService,
    private onlineBankingAuthorizationService: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService,
    private onlineBankingConsentService: OnlineBankingConsentsService,
    private onlineBankingCancService: OnlineBankingPaymentCancService
  ) {}

  /*****
   *
   * Online banking account infos
   * **/
  public getAccounts(userLogin?: string): Observable<AccountDetailsTO[]> {
    const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
    return this.onlineBankingAccountInfoService.accountsUsingGET(login);
  }

  public getAccount(accountID: string): Observable<AccountDetailsTO> {
    return this.onlineBankingAccountInfoService.accountUsingGET(accountID);
  }

  public getTransactions(
    params: OnlineBankingAccountInformationService.TransactionsUsingGETParams
  ): Observable<CustomPageImplTransactionTO> {
    return this.onlineBankingAccountInfoService.transactionsUsingGET(params);
  }

  /***
   * Online banking consents
   * */

  public getConsentsPaged(
    params: OnlineBankingConsentsService.PagedUsingGetParams,
    userLogin?: string
  ): Observable<CustomPageImplObaAisConsent> {
    const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
    return this.onlineBankingConsentService.consentsPagedUsingGET(
      login,
      params
    );
  }

  public revokeConsent(consentId: string): Observable<boolean> {
    return this.onlineBankingConsentService.revokeConsentUsingPUT(consentId);
  }

  public getPeriodicPaymentsPaged(
    params: OnlineBankingConsentsService.PagedUsingGetParams,
    userLogin?: string
  ): Observable<CustomPageImplPaymentTO> {
    const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
    return this.onlineBankingAccountInfoService.getPendingPeriodicPaymentsUsingGET(
      login,
      params
    );
  }

  public startCancellation(
    paymentId: string
  ): Observable<SCAPaymentResponseTO> {
    return this.onlineBankingCancService.initiatePaymentCancellation(paymentId);
  }

  public startScaNselectMethod(
    paymentId: string,
    methodId: string,
    cancellationId?: string
  ): Observable<SCAPaymentResponseTO> {
    return this.onlineBankingCancService.startSca(
      paymentId,
      methodId,
      cancellationId
    );
  }

  public validateTanAndExecuteCancellation(
    paymentId: string,
    authCode: string,
    cancellationId: string
  ): Observable<any> {
    return this.onlineBankingCancService.verifyCode(
      paymentId,
      authCode,
      cancellationId
    );
  }
}
