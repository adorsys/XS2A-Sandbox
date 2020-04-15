import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map } from 'rxjs/operators';
import { CustomizeService } from './customize.service';
import { SLICE_DATE_FROM_ISO_STRING } from '../components/common/constant/constants';
import { Theme } from '../models/theme.model';

@Injectable({
  providedIn: 'root',
})
export class JsonService {
  customExamplesSource;
  defaultExamplesSource;

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

  constructor(private http: HttpClient, private customizeService: CustomizeService) {
    if (this.customizeService.currentTheme) {
      this.customizeService.currentTheme.subscribe((theme: Theme) => {
        if (
          theme.pagesSettings &&
          theme.pagesSettings.playWithDataSettings &&
          theme.pagesSettings.playWithDataSettings.examplesCurrency &&
          theme.pagesSettings.playWithDataSettings.examplesCurrency.length > 0
        ) {
          this.currency = theme.pagesSettings.playWithDataSettings.examplesCurrency;
        }

        this.customExamplesSource = `${this.customizeService.customContentFolder}/examples`;
        this.defaultExamplesSource = `${this.customizeService.defaultContentFolder}/examples`;
      });
    }
  }

  public getRawJsonData(url: string): Observable<any> {
    const path = '/jsons/' + url + '.json';
    return this.http.get(this.customExamplesSource + path).pipe(
      map((response) => {
        return response;
      }),
      catchError(() => {
        return this.http.get(this.defaultExamplesSource + path);
      })
    );
  }

  public getPreparedJsonData(url: string, sepa?: boolean) {
    return this.getRawJsonData(url).pipe(
      map((data) => {
        let json = this.updateCurrencyForJsonData(JSON.stringify(data, null, '\t'), sepa);
        json = this.updateRequestedExecutionDateForJsonData(json);

        return JSON.parse(this.updateValidUntilForJsonData(json));
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
    const today = new Date().toISOString().slice(0, SLICE_DATE_FROM_ISO_STRING);

    // replaces all the values of "requestedExecutionDate" key word in formatted jsons
    const regex = /(requestedExecutionDate"\s*:\s*"\s*)(.+)(")/g;

    return data.replace(regex, 'requestedExecutionDate": "' + today + '"');
  }

  private updateValidUntilForJsonData(data: string) {
    const nextMonth = new Date();
    nextMonth.setMonth(nextMonth.getMonth() + 1);

    // replaces all the values of "validUntil" key word in formatted jsons
    const regex = /(validUntil"\s*:\s*"\s*)(.+)(")/g;

    return data.replace(regex, 'validUntil": "' + nextMonth.toISOString().slice(0, SLICE_DATE_FROM_ISO_STRING) + '"');
  }

  public getRawXmlData(url: string) {
    const path = '/xmls/' + url + '.xml';
    return this.http.get(this.customExamplesSource + path, { responseType: 'text' }).pipe(
      map((response) => {
        if (response.includes('<!DOCTYPE html>')) {
          throw new Error('Custom source is not found and default html returned instead!');
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
            map((response) => {
              if (response.includes('<!DOCTYPE html>')) {
                throw new Error('Default source is not found and default html returned instead!');
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
      map((data) => {
        // replaces all the values of currency in xmls
        const regex = /(Ccy="\s*)(.+)(")/g;

        return this.updateRequestExecutionDateForXml(data.replace(regex, 'Ccy="' + this.currency + '"'));
      })
    );
  }

  private updateRequestExecutionDateForXml(data) {
    const today = new Date().toISOString().slice(0, SLICE_DATE_FROM_ISO_STRING);

    // replaces all the values of "ReqdExctnDt" key word in xmls
    const regex = /(ReqdExctnDt>\s*)(.+)(<\/)/g;

    return data.replace(regex, 'ReqdExctnDt>' + today + '</');
  }
}
