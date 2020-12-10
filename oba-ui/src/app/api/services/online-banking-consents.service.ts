/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { ObaAisConsent } from '../models/oba-ais-consent';
import {CustomPageImplObaAisConsent} from "../models/custom-page-impl-ais-consents";

/**
 * Oba Consent Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingConsentsService extends __BaseService {
  static readonly confirmUsingGETPath = '/api/v1/consents/confirm/{userLogin}/{consentId}/{authorizationId}/{tan}';
  static readonly revokeConsentUsingPUTPath = '/api/v1/consents/{consentId}';
  static readonly consentsUsingGETPath = '/api/v1/consents/{userLogin}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * @param params The `OnlineBankingConsentsService.ConfirmUsingGETParams` containing the following parameters:
   *
   * - `userLogin`: userLogin
   *
   * - `tan`: tan
   *
   * - `consentId`: consentId
   *
   * - `authorizationId`: authorizationId
   */
  confirmUsingGETResponse(params: OnlineBankingConsentsService.ConfirmUsingGETParams): __Observable<__StrictHttpResponse<null>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;




    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/consents/confirm/${params.userLogin}/${params.consentId}/${params.authorizationId}/${params.tan}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<null>;
      })
    );
  }
  /**
   * @param params The `OnlineBankingConsentsService.ConfirmUsingGETParams` containing the following parameters:
   *
   * - `userLogin`: userLogin
   *
   * - `tan`: tan
   *
   * - `consentId`: consentId
   *
   * - `authorizationId`: authorizationId
   */
  confirmUsingGET(params: OnlineBankingConsentsService.ConfirmUsingGETParams): __Observable<null> {
    return this.confirmUsingGETResponse(params).pipe(
      __map(_r => _r.body as null)
    );
  }

  /**
   * @param consentId consentId
   * @return OK
   */
  revokeConsentUsingPUTResponse(consentId: string): __Observable<__StrictHttpResponse<boolean>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    let req = new HttpRequest<any>(
      'PUT',
      this.rootUrl + `/api/v1/consents/${consentId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'text'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return (_r as HttpResponse<any>).clone({ body: (_r as HttpResponse<any>).body === 'true' }) as __StrictHttpResponse<boolean>
      })
    );
  }
  /**
   * @param consentId consentId
   * @return OK
   */
  revokeConsentUsingPUT(consentId: string): __Observable<boolean> {
    return this.revokeConsentUsingPUTResponse(consentId).pipe(
      __map(_r => _r.body as boolean)
    );
  }

  consentsPagedUsingGET(userLogin: string,  params: OnlineBankingConsentsService.PagedUsingGetParams): __Observable<CustomPageImplObaAisConsent> {
    return this.consentsPagedUsingGETResponse(userLogin, params).pipe(
      __map(_r => _r.body as CustomPageImplObaAisConsent)
    );
  }

  /**
   * @param userLogin userLogin
   * @return OK
   */
  consentsPagedUsingGETResponse(userLogin: string, params: OnlineBankingConsentsService.PagedUsingGetParams): __Observable<__StrictHttpResponse<Array<ObaAisConsent>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    if (params.size != null)
      __params = __params.set('size', params.size.toString());
    if (params.page != null)
      __params = __params.set('page', params.page.toString());

    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/consents/${userLogin}/paged`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<ObaAisConsent>>;
      })
    );
  }
}

module OnlineBankingConsentsService {

  /**
   * Parameters for confirmUsingGET
   */
  export interface ConfirmUsingGETParams {

    /**
     * userLogin
     */
    userLogin: string;

    /**
     * tan
     */
    tan: string;

    /**
     * consentId
     */
    consentId: string;

    /**
     * authorizationId
     */
    authorizationId: string;
  }

  /**
   * Parameters for consentsPagedUsingGET
   */
  export interface PagedUsingGetParams {

    /**
     * size
     */
    size?: number;

    /**
     * page
     */
    page?: number;
  }
}

export { OnlineBankingConsentsService }
