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

import { PaymentAuthorizeResponse } from '../models/payment-authorize-response';

/**
 * Pis Cancellation Controller
 */
@Injectable({
  providedIn: 'root',
})
class PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService extends __BaseService {
  static readonly authorisePaymentUsingPOSTPath =
    '/pis-cancellation/{encryptedPaymentId}/authorisation/{authorisationId}/authCode';
  static readonly pisDoneUsingGETPath =
    '/pis-cancellation/{encryptedPaymentId}/authorisation/{authorisationId}/done';
  static readonly loginUsingPOST2Path =
    '/pis-cancellation/{encryptedPaymentId}/authorisation/{authorisationId}/login';
  static readonly selectMethodUsingPOST1Path =
    '/pis-cancellation/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}';

  constructor(config: __Configuration, http: HttpClient) {
    super(config, http);
  }

  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `authCode`: authCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  authorisePaymentUsingPOSTResponse(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.authCode != null)
      __params = __params.set('authCode', params.authCode.toString());
    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'POST',
      this.rootUrl +
        `/pis-cancellation/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/authCode`,
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
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `authCode`: authCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  authorisePaymentUsingPOST(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams
  ): __Observable<PaymentAuthorizeResponse> {
    return this.authorisePaymentUsingPOSTResponse(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * This call provides the server with the opportunity to close this session and redirect the PSU to the TPP or close the application window.
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `oauth2`: oauth2
   *
   * - `authConfirmationCode`: authConfirmationCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  pisDoneUsingGETResponse(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.oauth2 != null)
      __params = __params.set('oauth2', params.oauth2.toString());
    if (params.authConfirmationCode != null)
      __params = __params.set(
        'authConfirmationCode',
        params.authConfirmationCode.toString()
      );
    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'GET',
      this.rootUrl +
        `/pis-cancellation/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/done`,
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
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * This call provides the server with the opportunity to close this session and redirect the PSU to the TPP or close the application window.
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `oauth2`: oauth2
   *
   * - `authConfirmationCode`: authConfirmationCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  pisDoneUsingGET(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGETParams
  ): __Observable<PaymentAuthorizeResponse> {
    return this.pisDoneUsingGETResponse(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOST2Response(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.pin != null)
      __params = __params.set('pin', params.pin.toString());
    if (params.login != null)
      __params = __params.set('login', params.login.toString());
    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'POST',
      this.rootUrl +
        `/pis-cancellation/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/login`,
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
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOST2(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params
  ): __Observable<PaymentAuthorizeResponse> {
    return this.loginUsingPOST2Response(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  selectMethodUsingPOST1Response(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    const __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'POST',
      this.rootUrl +
        `/pis-cancellation/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/methods/${params.scaMethodId}`,
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
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  selectMethodUsingPOST1(
    params: PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST1Params
  ): __Observable<PaymentAuthorizeResponse> {
    return this.selectMethodUsingPOST1Response(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }
}

namespace PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService {
  /**
   * Parameters for authorisePaymentUsingPOST
   */
  export interface AuthorisePaymentUsingPOSTParams {
    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * authorisationId
     */
    authorisationId: string;

    /**
     * authCode
     */
    authCode: string;

    /**
     * Cookie
     */
    Cookie?: string;
  }

  /**
   * Parameters for pisDoneUsingGET
   */
  export interface PisDoneUsingGETParams {
    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * authorisationId
     */
    authorisationId: string;

    /**
     * oauth2
     */
    oauth2?: boolean;

    /**
     * authConfirmationCode
     */
    authConfirmationCode?: string;

    /**
     * Cookie
     */
    Cookie?: string;
  }

  /**
   * Parameters for loginUsingPOST2
   */
  export interface LoginUsingPOST2Params {
    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * authorisationId
     */
    authorisationId: string;

    /**
     * pin
     */
    pin?: string;

    /**
     * login
     */
    login?: string;

    /**
     * Cookie
     */
    Cookie?: string;
  }

  /**
   * Parameters for selectMethodUsingPOST1
   */
  export interface SelectMethodUsingPOST1Params {
    /**
     * scaMethodId
     */
    scaMethodId: string;

    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * authorisationId
     */
    authorisationId: string;

    /**
     * Cookie
     */
    Cookie?: string;
  }
}

export { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService };
