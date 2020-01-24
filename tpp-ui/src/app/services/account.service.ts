import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {map} from "rxjs/operators";
import {environment} from '../../environments/environment';
import {AccountReport} from '../models/account-report';
import {Amount} from '../models/amount.model';
import {GrantAccountAccess} from '../models/grant-account-access.model';
import {PaginationResponse} from "../models/pagination-reponse";
import {Account} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {

  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {
  }

  getAccounts(page: number = 0, size: number = 25): Observable<{ accounts: Account[], totalElements: number }> {
    return this.http.get<PaginationResponse<Account[]>>(`${this.url}/accounts/page?page=${page}&size=${size}`).pipe(
      map((resp) => {
        return {
          accounts: resp.content,
          totalElements: resp.totalElements
        };
      })
    );
  }

  getAccount(id: string) {
    return this.http.get(this.url + '/accounts/' + id);
  }

  getAccountByIban(iban: string) {
    return this.http.get<Account>(this.url + '/accounts/details?iban=' + iban);
  }

  getAccountReport(id: string): Observable<AccountReport> {
    return this.http.get<AccountReport>(this.url + '/accounts/report/' + id);
  }

  createAccount(userId: string, account: Account) {
    return this.http.post(this.url + '/accounts', account, {
      params: {userId: userId}
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
}
