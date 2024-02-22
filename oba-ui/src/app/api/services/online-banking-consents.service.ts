/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
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

import { ObaAisConsent } from '../models/oba-ais-consent';
import { CustomPageImplObaAisConsent } from '../models/custom-page-impl-ais-consents';

/**
 * Oba Consent Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingConsentsService extends __BaseService {
  static readonly confirmUsingGETPath =
    '/api/v1/consents/confirm/{userLogin}/{consentId}/{authorizationId}/{tan}';
  static readonly revokeConsentUsingPUTPath = '/api/v1/consents/{consentId}';
  static readonly consentsUsingGETPath = '/api/v1/consents/{userLogin}';

  constructor(config: __Configuration, http: HttpClient) {
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
  confirmUsingGETResponse(
    params: OnlineBankingConsentsService.ConfirmUsingGETParams
  ): __Observable<__StrictHttpResponse<null>> {
    const __params = this.newParams();
    const __headers = new HttpHeaders();
    const __body: any = null;

    const req = new HttpRequest<any>(
      'GET',
      this.rootUrl +
        `/api/v1/consents/confirm/${params.userLogin}/${params.consentId}/${params.authorizationId}/${params.tan}`,
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
  confirmUsingGET(
    params: OnlineBankingConsentsService.ConfirmUsingGETParams
  ): __Observable<null> {
    return this.confirmUsingGETResponse(params).pipe(
      __map((_r) => _r.body as null)
    );
  }

  /**
   * @param consentId consentId
   * @return OK
   */
  revokeConsentUsingPUTResponse(
    consentId: string
  ): __Observable<__StrictHttpResponse<boolean>> {
    const __params = this.newParams();
    const __headers = new HttpHeaders();
    const __body: any = null;

    const req = new HttpRequest<any>(
      'PUT',
      this.rootUrl + `/api/v1/consents/${consentId}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'text',
      }
    );

    return this.http.request<any>(req).pipe(
      __filter((_r) => _r instanceof HttpResponse),
      __map((_r) => {
        return (_r as HttpResponse<any>).clone({
          body: (_r as HttpResponse<any>).body === 'true',
        }) as __StrictHttpResponse<boolean>;
      })
    );
  }
  /**
   * @param consentId consentId
   * @return OK
   */
  revokeConsentUsingPUT(consentId: string): __Observable<boolean> {
    return this.revokeConsentUsingPUTResponse(consentId).pipe(
      __map((_r) => _r.body as boolean)
    );
  }

  consentsPagedUsingGET(
    userLogin: string,
    params: OnlineBankingConsentsService.PagedUsingGetParams
  ): __Observable<CustomPageImplObaAisConsent> {
    return this.consentsPagedUsingGETResponse(userLogin, params).pipe(
      __map((_r) => _r.body as CustomPageImplObaAisConsent)
    );
  }

  /**
   * @param userLogin userLogin
   * @return OK
   */
  consentsPagedUsingGETResponse(
    userLogin: string,
    params: OnlineBankingConsentsService.PagedUsingGetParams
  ): __Observable<__StrictHttpResponse<Array<ObaAisConsent>>> {
    let __params = this.newParams();
    const __headers = new HttpHeaders();
    const __body: any = null;

    // todo as an option for replace below strings
    // const param = ['size', 'page'];
    // for(let i=0; i< param.length; i++) {
    //   if(params[param[i]] != null) {
    //     __params = __params.set(param[i], params[param[i]].toString())
    //   }
    // }

    if (params.size != null)
      __params = __params.set('size', params.size.toString());
    if (params.page != null)
      __params = __params.set('page', params.page.toString());

    // todo as an option for replace below strings
    // return this.http.get(this.rootUrl + `/api/v1/consents/${userLogin}/paged`, __body)

    const req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/consents/${userLogin}/paged`,
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
        return _r as __StrictHttpResponse<Array<ObaAisConsent>>;
      })
    );
  }
}

namespace OnlineBankingConsentsService {
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

export { OnlineBankingConsentsService };
