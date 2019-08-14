/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { AccountDetailsTO } from '../models/account-details-to';
import { TransactionTO } from '../models/transaction-to';

/**
 * Oba Ais Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingAccountInformationService extends __BaseService {
  static readonly accountsUsingGETPath = '/api/v1/ais/accounts/{userLogin}';
  static readonly transactionsUsingGETPath = '/api/v1/ais/transactions/{accountId}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * @param userLogin userLogin
   * @return OK
   */
  accountsUsingGETResponse(userLogin: string): __Observable<__StrictHttpResponse<Array<AccountDetailsTO>>> {
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
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
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
      __map(_r => _r.body as Array<AccountDetailsTO>)
    );
  }

  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGETParams` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */
  transactionsUsingGETResponse(params: OnlineBankingAccountInformationService.TransactionsUsingGETParams): __Observable<__StrictHttpResponse<Array<TransactionTO>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    if (params.dateTo != null) __params = __params.set('dateTo', params.dateTo.toString());
    if (params.dateFrom != null) __params = __params.set('dateFrom', params.dateFrom.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/ais/transactions/${params.accountId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<TransactionTO>>;
      })
    );
  }
  /**
   * @param params The `OnlineBankingAccountInformationService.TransactionsUsingGETParams` containing the following parameters:
   *
   * - `accountId`: accountId
   *
   * - `dateTo`: dateTo
   *
   * - `dateFrom`: dateFrom
   *
   * @return OK
   */
  transactionsUsingGET(params: OnlineBankingAccountInformationService.TransactionsUsingGETParams): __Observable<Array<TransactionTO>> {
    return this.transactionsUsingGETResponse(params).pipe(
      __map(_r => _r.body as Array<TransactionTO>)
    );
  }
}

module OnlineBankingAccountInformationService {

  /**
   * Parameters for transactionsUsingGET
   */
  export interface TransactionsUsingGETParams {

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
}

export { OnlineBankingAccountInformationService }
