/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AccountReport } from '../models/account-report';
import { Amount } from '../models/amount.model';
import { GrantAccountAccess } from '../models/grant-account-access.model';
import { PaginationResponse } from '../models/pagination-reponse';
import { Account } from '../models/account.model';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {}

  getAccounts(page = 0, size = 25, queryParam = ''): Observable<{ accounts: Account[]; totalElements: number }> {
    return this.http
      .get<PaginationResponse<Account[]>>(`${this.url}/accounts/page?page=${page}&size=${size}&queryParam=${queryParam}&withBalance=true`)
      .pipe(
        map((resp) => {
          return {
            accounts: resp.content,
            totalElements: resp.totalElements,
          };
        })
      );
  }

  getAccount(id: string) {
    return this.http.get(this.url + '/accounts/' + id);
  }

  getAccountReport(id: string): Observable<AccountReport> {
    return this.http.get<AccountReport>(this.url + '/accounts/report/' + id);
  }

  createAccount(userId: string, account: Account) {
    return this.http.post(this.url + '/accounts', account, {
      params: { userId: userId },
    });
  }

  depositCash(accountId: string, amount: Amount) {
    return this.http.post(this.url + '/accounts/' + accountId + '/deposit-cash', amount);
  }

  updateAccountAccessForUser(accountAccess: GrantAccountAccess) {
    return this.http.put(this.url + '/accounts/access', accountAccess);
  }

  getCurrencies() {
    return this.http.get(this.url + '/currencies');
  }

  blockAccount(accountId: string) {
    return this.http.post(this.url + `/accounts/status?accountId=${accountId}`, accountId);
  }

  deleteAccount(accountId: string) {
    return this.http.delete(this.url + `/account/${accountId}`);
  }

  setCreditLimit(accountId: string, creditLimit: bigint) {
    return this.http.put(this.url + '/accounts/credit', creditLimit, {
      headers: { 'Content-Type': 'application/json' },
      params: { accountId: accountId },
    });
  }
}
