/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { AuthorizeResponse } from '../models/authorize-response';
import { PaymentAuthorizeResponse } from '../models/payment-authorize-response';

/**
 * Provides access to online banking payment functionality
 */
@Injectable({
  providedIn: 'root',
})
class PSUPISService extends __BaseService {
  static readonly pisAuthUsingGETPath = '/pis/auth';
  static readonly authrizedPaymentUsingPOSTPath = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/authCode';
  static readonly initiatePaymentUsingPOSTPath = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/initiate';
  static readonly loginUsingPOST1Path = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/login';
  static readonly selectMethodUsingPOST1Path = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * @param params The `PSUPISService.PisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * @return OK
   */
  pisAuthUsingGETResponse(params: PSUPISService.PisAuthUsingGETParams): __Observable<__StrictHttpResponse<AuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.redirectId != null) __params = __params.set('redirectId', params.redirectId.toString());
    if (params.encryptedPaymentId != null) __params = __params.set('encryptedPaymentId', params.encryptedPaymentId.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/pis/auth`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<AuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISService.PisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * @return OK
   */
  pisAuthUsingGET(params: PSUPISService.PisAuthUsingGETParams): __Observable<AuthorizeResponse> {
    return this.pisAuthUsingGETResponse(params).pipe(
      __map(_r => _r.body as AuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISService.AuthrizedPaymentUsingPOSTParams` containing the following parameters:
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
  authrizedPaymentUsingPOSTResponse(params: PSUPISService.AuthrizedPaymentUsingPOSTParams): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;


    if (params.authCode != null) __params = __params.set('authCode', params.authCode.toString());
    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/authCode`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISService.AuthrizedPaymentUsingPOSTParams` containing the following parameters:
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
  authrizedPaymentUsingPOST(params: PSUPISService.AuthrizedPaymentUsingPOSTParams): __Observable<PaymentAuthorizeResponse> {
    return this.authrizedPaymentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISService.InitiatePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  initiatePaymentUsingPOSTResponse(params: PSUPISService.InitiatePaymentUsingPOSTParams): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;


    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/initiate`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISService.InitiatePaymentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  initiatePaymentUsingPOST(params: PSUPISService.InitiatePaymentUsingPOSTParams): __Observable<PaymentAuthorizeResponse> {
    return this.initiatePaymentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISService.LoginUsingPOST1Params` containing the following parameters:
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOST1Response(params: PSUPISService.LoginUsingPOST1Params): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.pin != null) __params = __params.set('pin', params.pin.toString());
    if (params.login != null) __params = __params.set('login', params.login.toString());


    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/login`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISService.LoginUsingPOST1Params` containing the following parameters:
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOST1(params: PSUPISService.LoginUsingPOST1Params): __Observable<PaymentAuthorizeResponse> {
    return this.loginUsingPOST1Response(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUPISService.SelectMethodUsingPOST1Params` containing the following parameters:
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
  selectMethodUsingPOST1Response(params: PSUPISService.SelectMethodUsingPOST1Params): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;



    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/methods/${params.scaMethodId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<PaymentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUPISService.SelectMethodUsingPOST1Params` containing the following parameters:
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
  selectMethodUsingPOST1(params: PSUPISService.SelectMethodUsingPOST1Params): __Observable<PaymentAuthorizeResponse> {
    return this.selectMethodUsingPOST1Response(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }
}

module PSUPISService {

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
   * Parameters for loginUsingPOST1
   */
  export interface LoginUsingPOST1Params {

    /**
     * pin
     */
    pin: string;

    /**
     * login
     */
    login: string;

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

export { PSUPISService }
