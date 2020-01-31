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

  public getPreparedJsonData(url: string, sepa?: boolean) {
    return this.getRawJsonData(url).pipe(
      map(data => {
        let json = this.updateCurrencyForJsonData(
          JSON.stringify(data, null, '\t'),
          sepa
        );
        json = this.updateRequestedExecutionDateForJsonData(json);

        return JSON.parse(json);
      })
    );
  }

  private updateCurrencyForJsonData(data, sepa?: boolean) {
    // replaces all the values of "currency" key word in formatted jsons
    const regex = /(currency"\s*:\s*"\s*)(.+)(")/g;
    // currency is EUR in sepa-credit-tranfers payments due to the requirements for such payments
    const currency = sepa ? 'EUR' : this.currency;

    return data.replace(regex, 'currency": "' + currency + '"');
  }

  private updateRequestedExecutionDateForJsonData(data: string) {
    const today = new Date().toISOString().slice(0, 10);

    // replaces all the values of "requestedExecutionDate" key word in formatted jsons
    const regex = /(requestedExecutionDate"\s*:\s*"\s*)(.+)(")/g;

    return data.replace(regex, 'requestedExecutionDate": "' + today + '"');
  }

  public getRawXmlData(url: string) {
    const path = 'xmls/' + url + '.xml';
    return this.http
      .get(this.customExamplesSource + path, { responseType: 'text' })
      .pipe(
        map(response => {
          if (response.includes('<!DOCTYPE html>')) {
            throw new Error(
              'Custom source is not found and default html returned instead!'
            );
          } else {
            return response;
          }
        }),
        catchError(() => {
          return this.http
            .get(this.defaultExamplesSource + path, {
              responseType: 'text',
            })
            .pipe(
              map(response => {
                if (response.includes('<!DOCTYPE html>')) {
                  throw new Error(
                    'Default source is not found and default html returned instead!'
                  );
                } else {
                  return response;
                }
              }),
              catchError(() => {
                return '';
              })
            );
        })
      );
  }

  public getPreparedXmlData(url: string) {
    return this.getRawXmlData(url).pipe(
      map(data => {
        // replaces all the values of currency in xmls
        const regex = /(Ccy="\s*)(.+)(")/g;

        return this.updateRequestExecutionDateForXml(
          data.replace(regex, 'Ccy="' + this.currency + '"')
        );
      })
    );
  }

  private updateRequestExecutionDateForXml(data) {
    const today = new Date().toISOString().slice(0, 10);

    // replaces all the values of "ReqdExctnDt" key word in xmls
    const regex = /(ReqdExctnDt>\s*)(.+)(<\/)/g;

    return data.replace(regex, 'ReqdExctnDt>' + today + '</');
  }
}
