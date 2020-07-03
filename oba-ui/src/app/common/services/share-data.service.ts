import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import {
  ConsentAuthorizeResponse,
  PaymentAuthorizeResponse,
  UserTO,
} from '../../api/models';

@Injectable({
  providedIn: 'root',
})
export class ShareDataService {
  // update user detail
  private user = new BehaviorSubject(null);
  currentUser = this.user.asObservable();

  // response data
  private data = new BehaviorSubject<
    ConsentAuthorizeResponse | PaymentAuthorizeResponse
  >(null);
  currentData = this.data.asObservable();

  // oauth2 param
  private oauth = new BehaviorSubject<boolean>(null);
  oauthParam = this.oauth.asObservable();

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

  constructor() {}

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
