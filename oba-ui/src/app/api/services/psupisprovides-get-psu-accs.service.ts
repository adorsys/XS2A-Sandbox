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

  private isSubmitted = new BehaviorSubject<boolean>(false);

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

  getIsSubmitted(): Observable<boolean> {
    return this.isSubmitted.asObservable();
  }

  set setIsSubmitted(bool: boolean) {
    this.isSubmitted.next(bool);
  }

  // tslint:disable-next-line:adjacent-overload-signatures
  set choseIbanAndCurrency(obj) {
    this.ibanAndCurrency.next(obj);
  }
}
