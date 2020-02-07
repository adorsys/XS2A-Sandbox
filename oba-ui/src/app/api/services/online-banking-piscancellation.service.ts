/* tslint:disable */
import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpResponse, HttpHeaders } from '@angular/common/http';
import { BaseService as __BaseService } from '../base-service';
import { ApiConfiguration as __Configuration } from '../api-configuration';
import { StrictHttpResponse as __StrictHttpResponse } from '../strict-http-response';
import { Observable as __Observable } from 'rxjs';
import { map as __map, filter as __filter } from 'rxjs/operators';

import { SCAPaymentResponseTO } from '../models/scapayment-response-to';

/**
 * Oba Cancellation Controller
 */
@Injectable({
  providedIn: 'root',
})
class OnlineBankingPISCancellationService extends __BaseService {
  static readonly initCancellationUsingPOSTPath = '/api/v1/payment/cancellation';
  static readonly validateTANUsingPUTPath = '/api/v1/payment/cancellation/confirmation';
  static readonly selectScaUsingPOSTPath = '/api/v1/payment/cancellation/sca';

  constructor(
    config: __Configuration,
    http: HttpClient
  ) {
    super(config, http);
  }

  /**
   * @param paymentId paymentId
   * @return OK
   */
  initCancellationUsingPOSTResponse(paymentId: string): __Observable<__StrictHttpResponse<SCAPaymentResponseTO>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (paymentId != null) __params = __params.set('paymentId', paymentId.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/api/v1/payment/cancellation`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<SCAPaymentResponseTO>;
      })
    );
  }
  /**
   * @param paymentId paymentId
   * @return OK
   */
  initCancellationUsingPOST(paymentId: string): __Observable<SCAPaymentResponseTO> {
    return this.initCancellationUsingPOSTResponse(paymentId).pipe(
      __map(_r => _r.body as SCAPaymentResponseTO)
    );
  }

  /**
   * @param params The `OnlineBankingPISCancellationService.ValidateTANUsingPUTParams` containing the following parameters:
   *
   * - `paymentId`: paymentId
   *
   * - `cancellationId`: cancellationId
   *
   * - `authCode`: authCode
   */
  validateTANUsingPUTResponse(params: OnlineBankingPISCancellationService.ValidateTANUsingPUTParams): __Observable<__StrictHttpResponse<null>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.paymentId != null) __params = __params.set('paymentId', params.paymentId.toString());
    if (params.cancellationId != null) __params = __params.set('cancellationId', params.cancellationId.toString());
    if (params.authCode != null) __params = __params.set('authCode', params.authCode.toString());
    let req = new HttpRequest<any>(
      'PUT',
      this.rootUrl + `/api/v1/payment/cancellation/confirmation`,
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
   * @param params The `OnlineBankingPISCancellationService.ValidateTANUsingPUTParams` containing the following parameters:
   *
   * - `paymentId`: paymentId
   *
   * - `cancellationId`: cancellationId
   *
   * - `authCode`: authCode
   */
  validateTANUsingPUT(params: OnlineBankingPISCancellationService.ValidateTANUsingPUTParams): __Observable<null> {
    return this.validateTANUsingPUTResponse(params).pipe(
      __map(_r => _r.body as null)
    );
  }

  /**
   * @param params The `OnlineBankingPISCancellationService.SelectScaUsingPOSTParams` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `paymentId`: paymentId
   *
   * - `cancellationId`: cancellationId
   *
   * @return OK
   */
  selectScaUsingPOSTResponse(params: OnlineBankingPISCancellationService.SelectScaUsingPOSTParams): __Observable<__StrictHttpResponse<SCAPaymentResponseTO>> {
    let __params = this.newParams();
    let __headers = new HttpHeaders();
    let __body: any = null;
    if (params.scaMethodId != null) __params = __params.set('scaMethodId', params.scaMethodId.toString());
    if (params.paymentId != null) __params = __params.set('paymentId', params.paymentId.toString());
    if (params.cancellationId != null) __params = __params.set('cancellationId', params.cancellationId.toString());
    let req = new HttpRequest<any>(
      'POST',
      this.rootUrl + `/api/v1/payment/cancellation/sca`,
      __body,
      {
        headers: __headers,
        params: __params,
        responseType: 'json'
      });

    return this.http.request<any>(req).pipe(
      __filter(_r => _r instanceof HttpResponse),
      __map((_r) => {
        return _r as __StrictHttpResponse<SCAPaymentResponseTO>;
      })
    );
  }
  /**
   * @param params The `OnlineBankingPISCancellationService.SelectScaUsingPOSTParams` containing the following parameters:
   *
   * - `scaMethodId`: scaMethodId
   *
   * - `paymentId`: paymentId
   *
   * - `cancellationId`: cancellationId
   *
   * @return OK
   */
  selectScaUsingPOST(params: OnlineBankingPISCancellationService.SelectScaUsingPOSTParams): __Observable<SCAPaymentResponseTO> {
    return this.selectScaUsingPOSTResponse(params).pipe(
      __map(_r => _r.body as SCAPaymentResponseTO)
    );
  }
}

module OnlineBankingPISCancellationService {

  /**
   * Parameters for validateTANUsingPUT
   */
  export interface ValidateTANUsingPUTParams {

    /**
     * paymentId
     */
    paymentId: string;

    /**
     * cancellationId
     */
    cancellationId: string;

    /**
     * authCode
     */
    authCode: string;
  }

  /**
   * Parameters for selectScaUsingPOST
   */
  export interface SelectScaUsingPOSTParams {

    /**
     * scaMethodId
     */
    scaMethodId: string;

    /**
     * paymentId
     */
    paymentId: string;

    /**
     * cancellationId
     */
    cancellationId: string;
  }
}

export { OnlineBankingPISCancellationService }
