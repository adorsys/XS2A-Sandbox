import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class JsonService {
  customJsonSource: string = '../assets/UI/custom/jsons/';
  defaultJsonSource: string = '../assets/UI/jsons/';

  jsonLinks = {
    singlePayment: 'singlePayment.json',
    periodicPayment: 'periodicPayment.json',
    bulkPayment: 'bulkPayment.json',
    debtorAccount: 'debtorAccount.json',
    singlePaymentPlayWithData: 'playWithDataSinglePayment.json',
    dedicatedAccountsConsent: 'dedicatedAccountsConsent.json',
    bankOfferedConsent: 'bankOfferedConsent.json',
    globalConsent: 'globalConsent.json',
    availableAccountsConsent: 'availableAccountsConsent.json',
    availableAccountsConsentWithBalance:
      'availableAccountsConsentWithBalance.json',
    consent: 'consent.json',
    psuData: 'psuData.json',
    scaAuthenticationData: 'scaAuthenticationData.json',
    authenticationMethodId: 'authenticationMethodId.json',
  };

  constructor(private http: HttpClient) {}

  public getJsonData(url: string): Observable<any> {
    return this.http.get(this.customJsonSource + url).pipe(
      map(response => {
        return response;
      }),
      catchError(error => {
        return this.http.get(this.defaultJsonSource + url);
      })
    );
  }
}
