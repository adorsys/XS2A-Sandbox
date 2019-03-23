/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { AccountDetailsTO } from '../models/account-details-to';
import { AuthorizeResponse } from '../models/authorize-response';
import { PIISConsentCreateResponse } from '../models/piisconsent-create-response';
import { PiisConsentRequest } from '../models/piis-consent-request';
import { ConsentAuthorizeResponse } from '../models/consent-authorize-response';

/**
 * Provides access to online banking account functionality
 */
@Injectable({
  providedIn: 'root',
})
class PSUAISService extends __BaseService {
  static readonly getListOfAccountsUsingGETPath = '/ais/accounts';
  static readonly aisAuthUsingGETPath = '/ais/auth';
  static readonly grantPiisConsentUsingPOSTPath = '/ais/piis';
  static readonly authrizedConsentUsingPOSTPath = '/ais/{encryptedConsentId}/authorisation/{authorisationId}/authCode';
  static readonly loginUsingPOSTPath = '/ais/{encryptedConsentId}/authorisation/{authorisationId}/login';
  static readonly selectMethodUsingPOSTPath = '/ais/{encryptedConsentId}/authorisation/{authorisationId}/methods/{scaMethodId}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * Returns the list of all accounts linked to the connected user. Call only available to role CUSTOMER.
   * @param Cookie Cookie
   * @return List of accounts accessible to the user.
   */
  getListOfAccountsUsingGETResponse(Cookie?: string): __Observable<__StrictHttpResponse<Array<AccountDetailsTO>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (Cookie != null) __headers = __headers.set('Cookie', Cookie.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/ais/accounts`,
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
   * Returns the list of all accounts linked to the connected user. Call only available to role CUSTOMER.
   * @param Cookie Cookie
   * @return List of accounts accessible to the user.
   */
  getListOfAccountsUsingGET(Cookie?: string): __Observable<Array<AccountDetailsTO>> {
    return this.getListOfAccountsUsingGETResponse(Cookie).pipe(
      __map(_r => _r.body as Array<AccountDetailsTO>)
    );
  }

  /**
   * @param params The `PSUAISService.AisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * @return OK
   */
  aisAuthUsingGETResponse(params: PSUAISService.AisAuthUsingGETParams): __Observable<__StrictHttpResponse<AuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.redirectId != null) __params = __params.set('redirectId', params.redirectId.toString());
    if (params.encryptedConsentId != null) __params = __params.set('encryptedConsentId', params.encryptedConsentId.toString());
    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/ais/auth`,
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
   * @param params The `PSUAISService.AisAuthUsingGETParams` containing the following parameters:
   *
   * - `redirectId`: redirectId
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * @return OK
   */
  aisAuthUsingGET(params: PSUAISService.AisAuthUsingGETParams): __Observable<AuthorizeResponse> {
    return this.aisAuthUsingGETResponse(params).pipe(
      __map(_r => _r.body as AuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUAISService.GrantPiisConsentUsingPOSTParams` containing the following parameters:
   *
   * - `piisConsentRequestTO`: piisConsentRequestTO
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  grantPiisConsentUsingPOSTResponse(params: PSUAISService.GrantPiisConsentUsingPOSTParams): __Observable<__StrictHttpResponse<PIISConsentCreateResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    __body = params.piisConsentRequestTO;
    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/ais/piis`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<PIISConsentCreateResponse>;
      })
    );
  }
  /**
   * @param params The `PSUAISService.GrantPiisConsentUsingPOSTParams` containing the following parameters:
   *
   * - `piisConsentRequestTO`: piisConsentRequestTO
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  grantPiisConsentUsingPOST(params: PSUAISService.GrantPiisConsentUsingPOSTParams): __Observable<PIISConsentCreateResponse> {
    return this.grantPiisConsentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as PIISConsentCreateResponse)
    );
  }

  /**
   * @param params The `PSUAISService.AuthrizedConsentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `authCode`: authCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  authrizedConsentUsingPOSTResponse(params: PSUAISService.AuthrizedConsentUsingPOSTParams): __Observable<__StrictHttpResponse<ConsentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;


    if (params.authCode != null) __params = __params.set('authCode', params.authCode.toString());
    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/ais/${params.encryptedConsentId}/authorisation/${params.authorisationId}/authCode`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<ConsentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUAISService.AuthrizedConsentUsingPOSTParams` containing the following parameters:
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `authCode`: authCode
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  authrizedConsentUsingPOST(params: PSUAISService.AuthrizedConsentUsingPOSTParams): __Observable<ConsentAuthorizeResponse> {
    return this.authrizedConsentUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as ConsentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUAISService.LoginUsingPOSTParams` containing the following parameters:
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOSTResponse(params: PSUAISService.LoginUsingPOSTParams): __Observable<__StrictHttpResponse<ConsentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.pin != null) __params = __params.set('pin', params.pin.toString());
    if (params.login != null) __params = __params.set('login', params.login.toString());


    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/ais/${params.encryptedConsentId}/authorisation/${params.authorisationId}/login`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<ConsentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUAISService.LoginUsingPOSTParams` containing the following parameters:
   *
   * - `pin`: pin
   *
   * - `login`: login
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  loginUsingPOST(params: PSUAISService.LoginUsingPOSTParams): __Observable<ConsentAuthorizeResponse> {
    return this.loginUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as ConsentAuthorizeResponse)
    );
  }

  /**
   * @param params The `PSUAISService.SelectMethodUsingPOSTParams` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  selectMethodUsingPOSTResponse(params: PSUAISService.SelectMethodUsingPOSTParams): __Observable<__StrictHttpResponse<ConsentAuthorizeResponse>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;



    if (params.Cookie != null) __headers = __headers.set('Cookie', params.Cookie.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/ais/${params.encryptedConsentId}/authorisation/${params.authorisationId}/methods/${params.scaMethodId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<ConsentAuthorizeResponse>;
      })
    );
  }
  /**
   * @param params The `PSUAISService.SelectMethodUsingPOSTParams` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `encryptedConsentId`: encryptedConsentId
   *
   * - `authorisationId`: authorisationId
   *
   * - `Cookie`: Cookie
   *
   * @return OK
   */
  selectMethodUsingPOST(params: PSUAISService.SelectMethodUsingPOSTParams): __Observable<ConsentAuthorizeResponse> {
    return this.selectMethodUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as ConsentAuthorizeResponse)
    );
  }
}

module PSUAISService {

  /**
   * Parameters for aisAuthUsingGET
   */
  export interface AisAuthUsingGETParams {

    /**
     * redirectId
     */
    redirectId: string;

    /**
     * encryptedConsentId
     */
    encryptedConsentId: string;
  }

  /**
   * Parameters for grantPiisConsentUsingPOST
   */
  export interface GrantPiisConsentUsingPOSTParams {

    /**
     * piisConsentRequestTO
     */
    piisConsentRequestTO: PiisConsentRequest;

    /**
     * Cookie
     */
    Cookie?: string;
  }

  /**
   * Parameters for authrizedConsentUsingPOST
   */
  export interface AuthrizedConsentUsingPOSTParams {

    /**
     * encryptedConsentId
     */
    encryptedConsentId: string;

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
   * Parameters for loginUsingPOST
   */
  export interface LoginUsingPOSTParams {

    /**
     * pin
     */
    pin: string;

    /**
     * login
     */
    login: string;

    /**
     * encryptedConsentId
     */
    encryptedConsentId: string;

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
   * Parameters for selectMethodUsingPOST
   */
  export interface SelectMethodUsingPOSTParams {

    /**
     * scaMethodId
     */
    scaMethodId: string;

    /**
     * encryptedConsentId
     */
    encryptedConsentId: string;

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

export { PSUAISService }
