import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { CustomizeService } from './customize.service';

@Injectable({
  providedIn: 'root',
})
export class JsonService {
  customExamplesSource = '../assets/UI/custom/examples/';
  defaultExamplesSource = '../assets/UI/examples/';

  jsonLinks = {
    singlePayment: 'payments/sepa-credit-transfers',
    periodicPayment: 'periodic-payments/sepa-credit-transfers',
    bulkPayment: 'bulk-payments/sepa-credit-transfers',
    debtorAccount: 'debtorAccount',
    singlePaymentPlayWithData: 'playWithDataSinglePayment',
    dedicatedAccountsConsent: 'dedicatedAccountsConsent',
    bankOfferedConsent: 'bankOfferedConsent',
    globalConsent: 'globalConsent',
    availableAccountsConsent: 'availableAccountsConsent',
    availableAccountsConsentWithBalance: 'availableAccountsConsentWithBalance',
    consent: 'consent',
    psuData: 'psuData',
    scaAuthenticationData: 'scaAuthenticationData',
    authenticationMethodId: 'authenticationMethodId',
  };

  private currency = 'EUR';

  constructor(
    private http: HttpClient,
    private customizeService: CustomizeService
  ) {
    this.customizeService.getCurrency().then(currency => {
      if (currency && currency.length !== 0) {
        this.currency = currency;
      }
    });
  }

  public getRawJsonData(url: string): Observable<any> {
    const path = 'jsons/' + url + '.json';
    return this.http.get(this.customExamplesSource + path).pipe(
      map(response => {
        return response;
      }),
      catchError(() => {
        return this.http.get(this.defaultExamplesSource + path);
      })
    );
  }

  public getPreparedJsonData(url: string) {
    return this.getRawJsonData(url).pipe(
      map(data => {
        // replaces all the values of "currency" key word in formatted jsons
        const regex = /(currency"\s*:\s*"\s*)(.+)(")/g;

        return JSON.parse(
          JSON.stringify(data, null, '\t').replace(
            regex,
            'currency": "' + this.currency + '"'
          )
        );
      })
    );
  }

  public getRawXmlData(url: string) {
    const path = 'xmls/' + url + '.xml';
    return this.http
      .get(this.customExamplesSource + path, { responseType: 'text' })
      .pipe(
        map(response => {
          if (response.includes('<!DOCTYPE html>')) {
            throw new Error(
              'Source is not found, default html returned instead!'
            );
          } else {
            return response;
          }
        }),
        catchError(() => {
          return this.http.get(this.defaultExamplesSource + path, {
            responseType: 'text',
          });
        })
      );
  }

  public getPreparedXmlData(url: string) {
    return this.getRawXmlData(url).pipe(
      map(data => {
        // replaces all the values of currency in formatted jsons
        const regex = /(Ccy="\s*)(.+)(")/g;

        return data.replace(regex, 'Ccy="' + this.currency + '"');
      })
    );
  }
}
