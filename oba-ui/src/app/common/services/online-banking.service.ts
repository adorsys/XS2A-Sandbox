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
