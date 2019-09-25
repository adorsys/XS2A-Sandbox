import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

import { ConsentAuthorizeResponse, PaymentAuthorizeResponse } from '../../api/models';

@Injectable({
  providedIn: 'root'
})
export class ShareDataService {
  // response data
  private data = new BehaviorSubject<ConsentAuthorizeResponse | PaymentAuthorizeResponse>(null);
  currentData = this.data.asObservable();

  // operation type
  private operationType = new BehaviorSubject<string>(null);
  currentOperation = this.operationType.asObservable();

  // encrypted Consent ID
  private encryptedConsentId = new BehaviorSubject<string>(null);
  currentEncryptedConsentId = this.encryptedConsentId.asObservable();

  // encrypted Payment ID
  private paymentId = new BehaviorSubject<string>(null);
  currentPaymentId = this.paymentId.asObservable();

  // encrypted Payment ID
  private authorisationId = new BehaviorSubject<string>(null);
  currentAuthorisationId = this.authorisationId.asObservable();

  constructor() {
  }

  changeData(data: ConsentAuthorizeResponse) {
    this.data.next(data);
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
