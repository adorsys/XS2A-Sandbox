import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';

import {AccountDetailsTO} from '../../api/models/account-details-to';
import {ObaAisConsent} from '../../api/models/oba-ais-consent';
import {OnlineBankingAccountInformationService} from '../../api/services/online-banking-account-information.service';
import {OnlineBankingConsentsService} from '../../api/services/online-banking-consents.service';
import {AuthService} from './auth.service';
import {CustomPageImplTransactionTO} from '../../api/models/custom-page-impl-transaction-to';
import {OnlineBankingAuthorizationProvidesAccessToOnlineBankingService} from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';
import {PaymentTO} from '../../api/models/payment-to';
import {CustomPageImplObaAisConsent} from "../../api/models/custom-page-impl-ais-consents";
import {CustomPageImplPaymentTO} from "../../api/models/custom-page-impl-paayment-to";

@Injectable({
  providedIn: 'root',
})
export class OnlineBankingService {
  constructor(
    private authService: AuthService,
    private onlineBankingAccountInfoService: OnlineBankingAccountInformationService,
    private onlineBankingAuthorizationService: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService,
    private onlineBankingConsentService: OnlineBankingConsentsService
  ) {
  }

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

  public getConsentsPaged(params: OnlineBankingConsentsService.PagedUsingGetParams, userLogin?: string
  ): Observable<CustomPageImplObaAisConsent> {
    const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
    return this.onlineBankingConsentService.consentsPagedUsingGET(login, params);
  }

  public revokeConsent(consentId: string): Observable<boolean> {
    return this.onlineBankingConsentService.revokeConsentUsingPUT(consentId);
  }

  public getPeriodicPaymentsPaged(params: OnlineBankingConsentsService.PagedUsingGetParams, userLogin?: string
  ): Observable<CustomPageImplPaymentTO> {
    const login = userLogin ? userLogin : this.authService.getAuthorizedUser();
    return this.onlineBankingAccountInfoService.getPendingPeriodicPaymentsUsingGET(login, params);
  }
}
