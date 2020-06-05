import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { catchError, map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
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

  constructor(
    private http: HttpClient,
    private router: Router,
    private autoLogoutService: AutoLogoutService
  ) {}

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
      .pipe(
        map((loginResponse) =>
          loginResponse.headers.get(this.authTokenStorageKey)
        )
      );
  }

  isLoggedIn(): boolean {
    return !this.jwtHelperService.isTokenExpired(this.getAuthorizationToken());
  }

  logout() {
    this.autoLogoutService.resetMonitoringConfig();
    localStorage.clear();
    this.router.navigate(['/logout']);
  }

  getAuthorizationToken(): string {
    return localStorage.getItem(this.authTokenStorageKey);
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
    localStorage.setItem(this.authTokenStorageKey, token);
    this.setUsersAccessRights(this.jwtHelperService.decodeToken(token));
    console.log('log', this.jwtHelperService.decodeToken(token));
  }

  private setUsersAccessRights(loginResponse): void {
    let admin = false;
    if (loginResponse['role'] === 'SYSTEM') {
      admin = true;
    }
    localStorage.setItem(ADMIN_KEY, admin ? 'true' : 'false');
  }

  public login(credentials: any) {
    return this.authorize(credentials).pipe(
      map((jwt) => {
        if (jwt === undefined) {
          return false;
        }
        this.setAuthorisationToken(jwt);
        return true;
      })
    );
  }
}
