import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';
import {Observable, of} from 'rxjs';
import {JwtHelperService} from '@auth0/angular-jwt';
import {Router} from '@angular/router';
import {AutoLogoutService} from './auto-logout.service';
import {OnlineBankingAuthorizationService} from '../../api/services';
import LoginUsingPOST1Params = OnlineBankingAuthorizationService.LoginUsingPOST1Params;

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private authTokenStorageKey = 'access_token';
    private jwtHelperService = new JwtHelperService();

    constructor(private http: HttpClient,
                private router: Router,
                private autoLogoutService: AutoLogoutService,
                private onlineBankingAuthorizationService: OnlineBankingAuthorizationService) {
    }

    authorize(loginUsingPOST1Params: LoginUsingPOST1Params): Observable<string> {
      return this.onlineBankingAuthorizationService.loginUsingPOST1Response(loginUsingPOST1Params).pipe(
        map(authorisationResponse => authorisationResponse.headers.get(this.authTokenStorageKey))
      );
    }

    login(credentials: any): Observable<boolean> {
      return this.authorize(credentials).pipe(
        map(jwt => {
          if (jwt != undefined) {
            // this.autoLogoutService.initializeTokenMonitoring();
            localStorage.setItem(this.authTokenStorageKey, jwt);
            return true;
          }

          return false;
        }),
        catchError((error) => {
          console.log(error);
          return of(false);
        })
      );
    }

    isLoggedIn(): boolean {
      return !this.jwtHelperService.isTokenExpired(this.getAuthorizationToken());
    }

    logout() {
      this.autoLogoutService.resetMonitoringConfig();
      localStorage.removeItem(this.authTokenStorageKey);
      this.router.navigate(['/logout']);
    }

    getAuthorizedUser(): string {
      return this.jwtHelperService.decodeToken(this.getAuthorizationToken()).login;
    }

    getAuthorizationToken(): string {
      return localStorage.getItem(this.authTokenStorageKey);
    }
}
