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
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Credentials } from '../models/credentials.model';
import { Router } from '@angular/router';
import { AutoLogoutService } from './auto-logout.service';
import { TppInfo } from '../models/tpp-info.model';
import { ADMIN_KEY } from '../commons/constant/constant';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  public url = `${environment.tppBackend}`;
  private authTokenStorageKey = 'access_token';
  private jwtHelperService = new JwtHelperService();

  constructor(private http: HttpClient, private router: Router, private autoLogoutService: AutoLogoutService) {}

  authorize(credentials: Credentials): Observable<string> {
    return this.http
      .post<any>(
        this.url + '/login',
        {},
        {
          headers: new HttpHeaders({
            login: credentials.login,
            pin: credentials.pin,
          }),
          observe: 'response',
        }
      )
      .pipe(map((loginResponse) => loginResponse.headers.get(this.authTokenStorageKey)));
  }

  isLoggedIn(): boolean {
    const authorizationToken = this.getAuthorizationToken();
    if (authorizationToken && authorizationToken !== 'null' && authorizationToken !== null) {
      return true;
    } else {
      return false;
    }
  }

  logout() {
    this.autoLogoutService.resetMonitoringConfig();
    sessionStorage.clear();
    this.router.navigate(['/logout']);
  }

  getAuthorizationToken(): string {
    return sessionStorage.getItem(this.authTokenStorageKey);
  }

  register(tppInfo: TppInfo, countryCode: string): Observable<any> {
    if (countryCode !== undefined && countryCode !== null) {
      tppInfo.id = countryCode + '_' + tppInfo.id;
      return this.http.post(this.url + '/register', tppInfo);
    }
  }

  requestCodeForResetPassword(credentials: any): Observable<any> {
    return this.http.post(this.url + '/password', credentials);
  }

  changePassword(credentials: any): Observable<any> {
    return this.http.put(this.url + '/password', credentials);
  }

  getTppIdStructure(countryCode: string): Observable<any> {
    return this.http.get(this.url + '/country/codes/structure', {
      params: new HttpParams().set('countryCode', countryCode),
    });
  }

  public setAuthorisationToken(token: any) {
    sessionStorage.setItem(this.authTokenStorageKey, token);
    this.setUsersAccessRights(this.jwtHelperService.decodeToken(token));
  }

  private setUsersAccessRights(loginResponse): void {
    let admin = false;
    if (loginResponse != null && loginResponse['realm_access']['roles'].includes('SYSTEM')) {
      admin = true;
    }
    sessionStorage.setItem(ADMIN_KEY, admin ? 'true' : 'false');
  }

  public login(credentials: any) {
    return this.authorize(credentials).pipe(
      map((jwt) => {
        if (jwt === undefined || jwt == null) {
          return false;
        }
        this.setAuthorisationToken(jwt);
        return true;
      })
    );
  }
}
