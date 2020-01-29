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
 * PIS Controller
 */
@Injectable({
  providedIn: 'root',
})
class PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService extends __BaseService {
  static readonly pisAuthUsingGETPath = '/pis/auth';
  static readonly authrizedPaymentUsingPOSTPath = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/authCode';
  static readonly pisDoneUsingGET1Path = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/done';
  static readonly initiatePaymentUsingPOSTPath = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/initiate';
  static readonly loginUsingPOST3Path = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/login';
  static readonly selectMethodUsingPOST2Path = '/pis/{encryptedPaymentId}/authorisation/{authorisationId}/methods/{scaMethodId}';
  static readonly failPaymentAuthorisationUsingDELETEPath = '/pis/{encryptedPaymentId}/{authorisationId}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
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
  pisAuthUsingGETResponse(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams): __Observable<__StrictHttpResponse<AuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.redirectId != null) __params = __params.set('redirectId', params.redirectId.toString());
    if (params.encryptedPaymentId != null) __params = __params.set('encryptedPaymentId', params.encryptedPaymentId.toString());
    if (params.Authorization != null) __headers = __headers.set('Authorization', params.Authorization.toString());
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
  pisAuthUsingGET(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams): __Observable<AuthorizeResponse> {
    return this.pisAuthUsingGETResponse(params).pipe(
      __map(_r => _r.body as AuthorizeResponse)
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
  authrizedPaymentUsingPOSTResponse(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
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
  authrizedPaymentUsingPOST(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthrizedPaymentUsingPOSTParams): __Observable<PaymentAuthorizeResponse> {
    return this.authrizedPaymentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }

  /**
   * This call provides the server with the opportunity to close this session and redirect the PSU to the TPP or close the application window.
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params` containing the following parameters:
   *
   * - `forgetConsent`: forgetConsent
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `backToTpp`: backToTpp
   *
   * - `authorisationId`: authorisationId
   *
   * - `oauth2`: oauth2
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  pisDoneUsingGET1Response(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.forgetConsent != null) __params = __params.set('forgetConsent', params.forgetConsent.toString());

    if (params.backToTpp != null) __params = __params.set('backToTpp', params.backToTpp.toString());

    if (params.oauth2 != null) __params = __params.set('oauth2', params.oauth2.toString());
    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/authorisation/${params.authorisationId}/done`,
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
   * This call provides the server with the opportunity to close this session and redirect the PSU to the TPP or close the application window.
   * @param params The `PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params` containing the following parameters:
   *
   * - `forgetConsent`: forgetConsent
   *
   * - `encryptedPaymentId`: encryptedPaymentId
   *
   * - `backToTpp`: backToTpp
   *
   * - `authorisationId`: authorisationId
   *
   * - `oauth2`: oauth2
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  pisDoneUsingGET1(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params): __Observable<PaymentAuthorizeResponse> {
    return this.pisDoneUsingGET1Response(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
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
  initiatePaymentUsingPOSTResponse(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
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
  initiatePaymentUsingPOST(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.InitiatePaymentUsingPOSTParams): __Observable<PaymentAuthorizeResponse> {
    return this.initiatePaymentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
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
  loginUsingPOST3Response(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
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
  loginUsingPOST3(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params): __Observable<PaymentAuthorizeResponse> {
    return this.loginUsingPOST3Response(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
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
  selectMethodUsingPOST2Response(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
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
  selectMethodUsingPOST2(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.SelectMethodUsingPOST2Params): __Observable<PaymentAuthorizeResponse> {
    return this.selectMethodUsingPOST2Response(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
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
  failPaymentAuthorisationUsingDELETEResponse(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams): __Observable<__StrictHttpResponse<PaymentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;


    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'DELETE',
      this.rootUrl + `/pis/${params.encryptedPaymentId}/${params.authorisationId}`,
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
  failPaymentAuthorisationUsingDELETE(params: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.FailPaymentAuthorisationUsingDELETEParams): __Observable<PaymentAuthorizeResponse> {
    return this.failPaymentAuthorisationUsingDELETEResponse(params).pipe(
      __map(_r => _r.body as PaymentAuthorizeResponse)
    );
  }
}

module PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService {

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
     * forgetConsent
     */
    forgetConsent: string;

    /**
     * encryptedPaymentId
     */
    encryptedPaymentId: string;

    /**
     * backToTpp
     */
    backToTpp: string;

    /**
     * authorisationId
     */
    authorisationId: string;

    /**
     * oauth2
     */
    oauth2?: boolean;

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

export { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService }
