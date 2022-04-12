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
import { BehaviorSubject, Observable } from 'rxjs';
import {
  ConsentAuthorizeResponse,
  PaymentAuthorizeResponse,
} from '../../api/models';

@Injectable({
  providedIn: 'root',
})
export class ShareDataService {
  // update user detail
  private user = new BehaviorSubject(null);
  currentUser = this.user.asObservable();

  // response data
  // todo do 2 method instead of 1
  private data = new BehaviorSubject<
    ConsentAuthorizeResponse | PaymentAuthorizeResponse
  >(null);

  // oauth2 param
  private oauth = new BehaviorSubject<boolean>(null);

  // operation type
  private operationType = new BehaviorSubject<string>(null);

  // encrypted Consent ID
  private encryptedConsentId = new BehaviorSubject<string>(null);
  currentEncryptedConsentId = this.encryptedConsentId.asObservable();

  // encrypted Payment ID
  private paymentId = new BehaviorSubject<string>(null);
  currentPaymentId = this.paymentId.asObservable();

  // encrypted Payment ID
  private authorisationId = new BehaviorSubject<string>(null);
  currentAuthorisationId = this.authorisationId.asObservable();

  get currentOperation(): Observable<string> {
    return this.operationType.asObservable();
  }

  get oauthParam(): Observable<boolean> {
    return this.oauth.asObservable();
  }

  get currentData(): Observable<
    ConsentAuthorizeResponse | PaymentAuthorizeResponse
  > {
    return this.data.asObservable();
  }

  updateUserDetails(data) {
    this.user.next(data);
  }

  changeData(data: ConsentAuthorizeResponse) {
    this.data.next(data);
  }

  changePaymentData(data: PaymentAuthorizeResponse) {
    this.data.next(data);
  }

  setOauthParam(oauthParam: boolean) {
    this.oauth.next(oauthParam);
  }

  setOperationType(operation: string) {
    this.operationType.next(operation);
  }

  setEncryptedConsentId(encryptedConsentId: string) {
    this.encryptedConsentId.next(encryptedConsentId);
  }

  setPaymentId(paymentId: string) {
    this.paymentId.next(paymentId);
  }

  setAuthorisationId(authorisationId: string) {
    this.authorisationId.next(authorisationId);
  }
}
