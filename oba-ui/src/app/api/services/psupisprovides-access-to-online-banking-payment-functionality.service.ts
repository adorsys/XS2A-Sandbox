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
import {
  HttpClient,
  HttpHeaders,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable, Observable as __Observable } from 'rxjs';
import { filter as __filter, map as __map, map } from 'rxjs/operators';

import { AuthorizeResponse } from '../models/authorize-response';
import { PaymentAuthorizeResponse } from '../models/payment-authorize-response';
import { IPaginatorInterface } from '../../common/interfaces/paginator.interface';
import { AuthService } from '../../common/services/auth.service';
import {
  IPiisConsent,
  IPiisConsentContent,
} from '../../common/interfaces/piisConsent.interface';

/**
 * PIS Controller
 */
@Injectable({
  providedIn: 'root',
})
class PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService extends __BaseService {
  static readonly pisAuthUsingGETPath = '/pis/auth';
  static readonly authrizedPaymentUsingPOSTPath =
    '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/authCode';
  static readonly pisDoneUsingGET1Path =
    '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/done';
  static readonly initiatePaymentUsingPOSTPath =
    '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/initiate';
  static readonly loginUsingPOST3Path =
    '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/login';
  static readonly selectMethodUsingPOST2Path =
    '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}';
  static readonly failPaymentAuthorisationUsingDELETEPath =
    '/pis/{encryptedPaymentId}/{authorisationId}';

  constructor(
    config: __Configuration,
    http: HttpClient,
    private authService: AuthService
  ) {
    super(config, http);
  }

  getPiisConsents(
    config: Partial<IPaginatorInterface>
  ): Observable<IPiisConsent> {
    const login = this.authService.getAuthorizedUser();
    // todo mockDate must be deleted
    const mockData: IPiisConsentContent = {
      cmsPiisConsent: {
        account: {
          aspspAccountId: '123-DEDE89370400440532013000-EUR',
          bban: 89370400440532010000,
          cashAccountType: 'string',
          currency: 'EUR',
          iban: 'DE89370400440532013000',
          maskedPan: '2356xxxxxx1234',
          msisdn: '+49(0)911 360698-0',
          other: '30-163033-7',
          pan: '2356 5746 3217 1234',
          resourceId: 'string',
        },
        cardExpiryDate: 'string',
        cardInformation: 'string',
        cardNumber: 'string',
        consentStatus: 'RECEIVED',
        creationTimestamp: '2022-03-03T10:33:14.238Z',
        expireDate: 'string',
        id: 'string',
        instanceId: 'string',
        lastActionDate: 'string',
        psuData: {
          additionalPsuIdData: {
            psuAccept: 'string',
            psuAcceptCharset: 'string',
            psuAcceptEncoding: 'string',
            psuAcceptLanguage: 'string',
            psuDeviceId: 'string',
            psuGeoLocation: 'string',
            psuHttpMethod: 'string',
            psuIpPort: 'string',
            psuUserAgent: 'string',
          },
          psuCorporateId: 'string',
          psuCorporateIdType: 'string',
          psuId: 'string',
          psuIdType: 'string',
          psuIpAddress: 'string',
        },
        recurringIndicator: true,
        registrationInformation: 'string',
        requestDateTime: '2022-03-03T10:33:14.238Z',
        statusChangeTimestamp: '2022-03-03T10:33:14.238Z',
        tppAuthorisationNumber: 'string',
      },
      encryptedConsent: 'string',
    };

    return this.http
      .get<IPiisConsent>(
        this.rootUrl +
          `/api/v1/piis-consents/${login}/paged?size=${config.itemsPerPage}&page=${config.currentPage}`
      )
      .pipe(
        map((item) => {
          if (item.content.length <= 0) {
            item.content.push(mockData);
          }
          return item;
        })
      );
  }

  revokePiisConsents(consentId: string) {
    const login = this.authService.getAuthorizedUser();
    const body = null;
    return this.http.put(
      this.rootUrl + `/api/v1/piis-consents/${login}/${consentId}/revoke`,
      body
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `Authorization`: Authorization
   *
   * @return OK
   */
  pisAuthUsingGETResponse(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams
  ): __Observable<__StrictHttpResponse<AuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;
    if (params.redirectId != null)
      __params = __params.set('redirectId', params.redirectId.toString());
    if (params.encryptedPaymentId != null)
      __params = __params.set(
        'encryptedPaymentId',
        params.encryptedPaymentId.toString()
      );
    if (params.Authorization != null)
      __headers = __headers.set(
        'Authorization',
        params.Authorization.toString()
      );
    const req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/pis/auth`,
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
        return _r as __StrictHttpResponse<AuthorizeResponse>;
      })
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `Authorization`: Authorization
   *
   * @return OK
   */
  pisAuthUsingGET(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams
  ): __Observable<AuthorizeResponse> {
    return this.pisAuthUsingGETResponse(params).pipe(
      __map((_r) => _r.body as AuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams` containing the following parameters:
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
  authrizedPaymentUsingPOSTResponse(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams
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
        `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/authCode`,
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
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams` containing the following parameters:
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
  authrizedPaymentUsingPOST(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams
  ): __Observable<PaymentAuthorizeResponse> {
    return this.authrizedPaymentUsingPOSTResponse(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * This call provides the server with the opportunity to close this session and redirect the PSU to the TPP or close the application window.
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params` containing the following parameters:
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
  pisDoneUsingGET1Response(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params
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
        `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/done`,
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
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params` containing the following parameters:
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
  pisDoneUsingGET1(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params
  ): __Observable<PaymentAuthorizeResponse> {
    return this.pisDoneUsingGET1Response(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  initiatePaymentUsingPOSTResponse(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    const __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'POST',
      this.rootUrl +
        `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/initiate`,
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
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  initiatePaymentUsingPOST(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams
  ): __Observable<PaymentAuthorizeResponse> {
    return this.initiatePaymentUsingPOSTResponse(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params` containing the following parameters:
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
  loginUsingPOST3Response(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params
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
        `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/login`,
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
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params` containing the following parameters:
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
  loginUsingPOST3(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params
  ): __Observable<PaymentAuthorizeResponse> {
    return this.loginUsingPOST3Response(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params` containing the following parameters:
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
  selectMethodUsingPOST2Response(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    const __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'POST',
      this.rootUrl +
        `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/methods/${params.scaMethodId}`,
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
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params` containing the following parameters:
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
  selectMethodUsingPOST2(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params
  ): __Observable<PaymentAuthorizeResponse> {
    return this.selectMethodUsingPOST2Response(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * This call provides the server with the opportunity to close this session and revoke consent.
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  failPaymentAuthorisationUsingDELETEResponse(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams
  ): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    const __params = this.newParams();
    let __headers = new HttpHeaders();
    const __body: any = null;

    if (params.Cookie != null)
      __headers = __headers.set('Cookie', params.Cookie.toString());
    const req = new HttpRequest<any>(
      'DELETE',
      this.rootUrl +
        `/pis/${params.encryptedPaymentId}/${params.authorisationId}`,
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
   * This call provides the server with the opportunity to close this session and revoke consent.
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  failPaymentAuthorisationUsingDELETE(
    params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams
  ): __Observable<PaymentAuthorizeResponse> {
    return this.failPaymentAuthorisationUsingDELETEResponse(params).pipe(
      __map((_r) => _r.body as PaymentAuthorizeResponse)
    );
  }
}

namespace PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService {
  /**
   * Parameters for pisAuthUsingGET
   */
  export interface PisAuthUsingGETParams {
    /**
     * redirectId
     */
    redirectId: string;

    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * Authorization
     */
    Authorization?: string;
  }

  /**
   * Parameters for authrizedPaymentUsingPOST
   */
  export interface AuthrizedPaymentUsingPOSTParams {
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
   * Parameters for pisDoneUsingGET1
   */
  export interface PisDoneUsingGET1Params {
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
   * Parameters for initiatePaymentUsingPOST
   */
  export interface InitiatePaymentUsingPOSTParams {
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

  /**
   * Parameters for loginUsingPOST3
   */
  export interface LoginUsingPOST3Params {
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
   * Parameters for selectMethodUsingPOST2
   */
  export interface SelectMethodUsingPOST2Params {
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

  /**
   * Parameters for failPaymentAuthorisationUsingDELETE
   */
  export interface FailPaymentAuthorisationUsingDELETEParams {
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

export { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService };
