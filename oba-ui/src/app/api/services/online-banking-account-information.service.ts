/* tslint:disable */
import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpRequest,
  HttpResponse,
  HttpHeaders,
} from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { AccountDetailsTO } from '../models/account-details-to';
import { PaymentTO } from '../models/payment-to';
import { TransactionTO } from '../models/transaction-to';
import { CustomPageImplTransactionTO } from '../models/custom-page-impl-transaction-to';
import { UserTO } from '../models/user-to';

/**
 * Oba Ais Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingAccountInformationService extends __BaseService {
  static readonly accountUsingGETPath = '/api/v1/ais/account/{accountId}';
  static readonly accountsUsingGETPath = '/api/v1/ais/accounts/{userLogin}';
  static readonly getPendingPeriodicPaymentsUsingGETPath =
    '/api/v1/ais/payments';
  static readonly transactionsUsingGET1Path =
    '/api/v1/ais/transactions/{accountId}';
  static readonly transactionsUsingGETPath =
    '/api/v1/ais/transactions/{accountId}/page';

  constructor(config: __Configuration, http: HttpClient) {
    super(config, http);
  }

  /**
   * @param accountId accountId
   * @return OK
   */
  accountUsingGETResponse(
    accountId: string
  ): __Observable<__StrictHttpResponse<AccountDetailsTO>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/account/${accountId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json',
      }
    );

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<AccountDetailsTO>;
      })
    );
  }
  /**
   * @param accountId accountId
   * @return OK
   */
  accountUsingGET(accountId: string): __Observable<AccountDetailsTO> {
    return this.accountUsingGETResponse(accountId).pipe(
      __map((_r) => _r.body as AccountDetailsTO)
    );
  }

  /**
   * @param userLogin userLogin
   * @return OK
   */
  accountsUsingGETResponse(
    userLogin: string
  ): __Observable<__StrictHttpResponse<Array<AccountDetailsTO>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/accounts/${userLogin}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json',
      }
    );

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<AccountDetailsTO>>;
      })
    );
  }
  /**
   * @param userLogin userLogin
   * @return OK
   */
  accountsUsingGET(userLogin: string): __Observable<Array<AccountDetailsTO>> {
    return this.accountsUsingGETResponse(userLogin).pipe(
      __map((_r) => _r.body as Array<AccountDetailsTO>)
    );
  }

  /**
   * @return OK
   */
  getPendingPeriodicPaymentsUsingGETResponse(): __Observable<
    __StrictHttpResponse<Array<PaymentTO>>
  > {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/payments`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json',
      }
    );

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<PaymentTO>>;
      })
    );
  }
  /**
   * @return OK
   */
  getPendingPeriodicPaymentsUsingGET(): __Observable<Array<PaymentTO>> {
    return this.getPendingPeriodicPaymentsUsingGETResponse().pipe(
      __map((_r) => _r.body as Array<PaymentTO>)
    );
  }

  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGET1Params` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */
  transactionsUsingGET1Response(
    params: OnlineBankingAccountInformationService.TransactionsUsingGET1Params
  ): __Observable<__StrictHttpResponse<Array<TransactionTO>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    if (params.dateTo != null)
      __params = __params.set('dateTo', params.dateTo.toString());
    if (params.dateFrom != null)
      __params = __params.set('dateFrom', params.dateFrom.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/transactions/${params.accountId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json',
      }
    );

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<TransactionTO>>;
      })
    );
  }
  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGET1Params` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */
  transactionsUsingGET1(
    params: OnlineBankingAccountInformationService.TransactionsUsingGET1Params
  ): __Observable<Array<TransactionTO>> {
    return this.transactionsUsingGET1Response(params).pipe(
      __map((_r) => _r.body as Array<TransactionTO>)
    );
  }

  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGETParams` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `size`: size
   *
   * - `page`: page
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */
  transactionsUsingGETResponse(
    params: OnlineBankingAccountInformationService.TransactionsUsingGETParams
  ): __Observable<__StrictHttpResponse<CustomPageImplTransactionTO>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    if (params.size != null)
      __params = __params.set('size', params.size.toString());
    if (params.page != null)
      __params = __params.set('page', params.page.toString());
    if (params.dateTo != null)
      __params = __params.set('dateTo', params.dateTo.toString());
    if (params.dateFrom != null)
      __params = __params.set('dateFrom', params.dateFrom.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/transactions/${params.accountId}/page`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json',
      }
    );
    console.log('hello from service', req.body);
    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<CustomPageImplTransactionTO>;
      })
    );
  }
  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGETParams` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `size`: size
   *
   * - `page`: page
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */

  transactionsUsingGET(
    params: OnlineBankingAccountInformationService.TransactionsUsingGETParams
  ): __Observable<CustomPageImplTransactionTO> {
    return this.transactionsUsingGETResponse(params).pipe(
      __map((_r) => _r.body as CustomPageImplTransactionTO)
    );
  }

  getCurrentAccountInfo() {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    let req = new HttpRequest<any>('GET', this.rootUrl + `/api/v1/me`, __body, {
      headers: __headers,
      params: __params,
      responseType: 'json',
    });

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<UserTO>;
      })
    );
  }
}

module OnlineBankingAccountInformationService {
  /**
   * Parameters for transactionsUsingGET1
   */
  export interface TransactionsUsingGET1Params {
    /**
     * accountId
     */
    accountId: string;

    /**
     * dateTo
     */
    dateTo?: string;

    /**
     * dateFrom
     */
    dateFrom?: string;
  }

  /**
   * Parameters for transactionsUsingGET
   */
  export interface TransactionsUsingGETParams {
    /**
     * accountId
     */
    accountId: string;

    /**
     * size
     */
    size?: number;

    /**
     * page
     */
    page?: number;

    /**
     * dateTo
     */
    dateTo?: string;

    /**
     * dateFrom
     */
    dateFrom?: string;
  }
}

export { OnlineBankingAccountInformationService };
