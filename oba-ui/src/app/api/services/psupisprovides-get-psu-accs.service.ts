import { Injectable } from '@angular/core';
import { ApiConfiguration } from '../api-configuration';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PsupisprovidesGetPsuAccsService {
  private readonly getPisAccounts = '/pis/accounts';

  private ibanAndCurrency = new BehaviorSubject<{
    currency: string;
    iban: string;
  }>(null);

  constructor(private config: ApiConfiguration, private http: HttpClient) {}

  getAllIban(): Observable<any> {
    return this.http.get(`${this.config.rootUrl}${this.getPisAccounts}`);
  }

  sendPisInitiate(body, route) {
    return this.http.post(
      `${this.config.rootUrl}/pis/${route.encryptedPaymentId}/authorisation/${route.authorisationId}/initiate`,
      body
    );
  }

  get choseIbanAndCurrency(): { currency: string; iban: string } {
    return this.ibanAndCurrency.getValue();
  }

  choseIbanAndCurrencyObservable(): Observable<{
    currency: string;
    iban: string;
  }> {
    return this.ibanAndCurrency.asObservable();
  }

  // tslint:disable-next-line:adjacent-overload-signatures
  set choseIbanAndCurrency(obj) {
    this.ibanAndCurrency.next(obj);
  }
}
