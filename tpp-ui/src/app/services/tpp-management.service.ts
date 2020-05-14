import {Injectable} from '@angular/core';

import {environment} from '../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PaginationResponse} from '../models/pagination-reponse';
import {map} from 'rxjs/operators';
import {User, UserResponse} from '../models/user.model';
import {TppQueryParams, TppResponse} from '../models/tpp-management.model';
import {AccountResponse} from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class TppManagementService {

  public url = `${environment.tppBackend}`;
  private staffRole = 'STAFF';
  private customerRole = 'CUSTOMER';

  constructor(private http: HttpClient) {
  }

  changePin(tppId: string, newPin: string) {
    return this.http.put(`${this.url}/admin/password?tppId=${tppId}&pin=${newPin}`, null);
  }

  blockTpp(tppId: string) {
    return this.http.post(`${this.url}/admin/status?tppId=${tppId}`, null);
  }

  deleteTpp(tppId: string) {
    return this.http.delete(`${this.url}/admin?tppId=${tppId}`);
  }

  deleteSelf() {
    return this.http.delete(this.url + '/self');
  }

  deleteAccountTransactions(accountId: string) {
    return this.http.delete(this.url + '/account/' + accountId);
  }

  getUsersForTpp(tppId: string): Observable<User[]> {
    return this.getAllUsers(0, 100, {tppId: tppId}).pipe(
      map(resp => {
        return resp.users;
      }));
  }

  getTppById(tppId: string): Observable<User> {
    return this.getTpps(0, 1, {tppId: tppId}).pipe(
      map(data => {
          if (data && data.tpps && data.tpps.length > 0) {
            return data.tpps[0];
          } else {
            return undefined;
          }
        }
      ));
  }

  getTpps(page: number, size: number, queryParams?: TppQueryParams): Observable<TppResponse> {
    return this.getData(page, size, this.staffRole, false, queryParams).pipe(
      map(resp => {
        return {
          tpps: resp.content,
          totalElements: resp.totalElements
        };
      })
    );
  }

  getAllUsers(page: number, size: number, queryParams?: TppQueryParams): Observable<UserResponse> {
    return this.getData(page, size, this.customerRole, false, queryParams).pipe(
      map(resp => {
        return {
          users: resp.content,
          totalElements: resp.totalElements
        };
      })
    );
  }

  getAllAccounts(page: number, size: number, queryParams?: TppQueryParams): Observable<AccountResponse> {
    return this.getData(page, size, this.customerRole, true, queryParams).pipe(
      map(resp => {
        return {
          accounts: resp.content,
          totalElements: resp.totalElements
        };
      })
    );
  }

  private getData(page: number, size: number, role: string, accounts: boolean, queryParams?: TppQueryParams): Observable<any> {
    let params = new HttpParams();
    params = params.set('page', page.toLocaleString());
    params = params.set('size', size.toLocaleString());
    params = params.set('role', role);

    if (queryParams) {
      if (queryParams.userLogin) {
        params = params.set('userLogin', queryParams.userLogin);
      }
      if (queryParams.tppId) {
        params = params.set('tppId', queryParams.tppId);
      }
      if (queryParams.ibanParam) {
        params = params.set('ibanParam', queryParams.ibanParam);
      }
      if (queryParams.country) {
        params = params.set('country', queryParams.country);
      }
      if (queryParams.tppLogin) {
        params = params.set('tppLogin', queryParams.tppLogin);
      }
      if (queryParams.blocked) {
        params = params.set('blocked', JSON.stringify(queryParams.blocked));
      }
    }

    const endpoint = accounts ? 'account' : 'users';
    return this.http.get<PaginationResponse<User[]>>(`${this.url}/admin/${endpoint}`, {params: params});
  }
}
