import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { JwtHelperService } from '@auth0/angular-jwt';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { ResetPassword } from '../../api/models/reset-password';
import { SendCode } from '../../api/models/send-code';
import { UpdatePassword } from '../../api/models/update-password';
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services';
import { AutoLogoutService } from './auto-logout.service';

import LoginUsingPOST1Params = OnlineBankingAuthorizationProvidesAccessToOnlineBankingService.LoginUsingPOST1Params;
import { ShareDataService } from './share-data.service';
import { CurrentUserService } from './current-user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private authTokenStorageKey = 'access_token';
  private jwtHelperService = new JwtHelperService();

  constructor(
    private http: HttpClient,
    private router: Router,
    private autoLogoutService: AutoLogoutService,
    private onlineBankingAuthorizationService: OnlineBankingAuthorizationProvidesAccessToOnlineBankingService,
    private shareDataService: ShareDataService,
    private currentUserService: CurrentUserService
  ) {}

  authorize(loginUsingPOST1Params: LoginUsingPOST1Params): Observable<string> {
    return this.onlineBankingAuthorizationService
      .loginUsingPOST1Response(loginUsingPOST1Params)
      .pipe(
        map((authorisationResponse) =>
          authorisationResponse.headers.get(this.authTokenStorageKey)
        )
      );
  }

  login(credentials: any): Observable<boolean> {
    return this.authorize(credentials).pipe(
      map((jwt) => {
        if (jwt !== undefined) {
          this.autoLogoutService.initializeTokenMonitoring();
          localStorage.setItem(this.authTokenStorageKey, jwt);
          this.currentUserService
            .getCurrentUser()
            .subscribe((data) =>
              this.shareDataService.updateUserDetails(data.body)
            );
          return true;
        }
        return false;
      }),
      catchError((error) => {
        return throwError(error);
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
    return this.jwtHelperService.decodeToken(this.getAuthorizationToken())
      .login;
  }

  getAuthorizationToken(): string {
    return localStorage.getItem(this.authTokenStorageKey);
  }

  resetPassword(resetPassword: ResetPassword): Observable<UpdatePassword> {
    return this.onlineBankingAuthorizationService.updatePasswordUsingPUT(
      resetPassword
    );
  }

  requestCodeToResetPassword(
    resetPassword: ResetPassword
  ): Observable<SendCode> {
    return this.onlineBankingAuthorizationService.sendCodeUsingPOST(
      resetPassword
    );
  }
}
