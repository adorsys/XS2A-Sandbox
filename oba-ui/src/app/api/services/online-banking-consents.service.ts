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
 * Oba Consent Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingConsentsService extends __BaseService {
  static readonly revokeConsentUsingPUTPath = '/api/v1/consents/{consentId}';
  static readonly consentsUsingGETPath = '/api/v1/consents/{userLogin}';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
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

module OnlineBankingConsentsService {
}

export { OnlineBankingConsentsService }
