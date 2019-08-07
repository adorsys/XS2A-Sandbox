/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { AisAccountConsent } from '../models/ais-account-consent';

/**
 * Provides list of valid consents of current user
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingAISService extends __BaseService {
  static readonly consentsUsingGETPath = '/api/v1/consents/{userLogin}';

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
  consentsUsingGETResponse(userLogin: string): __Observable<__StrictHttpResponse<Array<AisAccountConsent>>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;

    let req = new HttpRequest<any>(
      'GET',
      this.rootUrl + `/api/v1/consents/${userLogin}`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<Array<AisAccountConsent>>;
      })
    );
  }
  /**
   * @param userLogin userLogin
   * @return OK
   */
  consentsUsingGET(userLogin: string): __Observable<Array<AisAccountConsent>> {
    return this.consentsUsingGETResponse(userLogin).pipe(
      __map(_r => _r.body as Array<AisAccountConsent>)
    );
  }
}

module OnlineBankingAISService {
}

export { OnlineBankingAISService }
