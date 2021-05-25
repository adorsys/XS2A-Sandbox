import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { from, Observable, throwError } from 'rxjs';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, tap } from 'rxjs/operators';
import { AutoLogoutService } from '../services/auto-logout.service';
import { CurrentUserService } from '../services/current-user.service';
import { ShareDataService } from '../services/share-data.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private authTokenStorageKey = 'access_token';

  constructor(
    private authService: AuthService,
    private autoLogoutService: AutoLogoutService,
    private currentUserService: CurrentUserService,
    private shareDataService: ShareDataService
  ) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return from(this.handleRequest(request, next)).pipe(
      tap((event: HttpEvent<any>) => {
        if (
          event instanceof HttpResponse &&
          event.headers.has(this.authTokenStorageKey)
        ) {
          this.saveAccessToken(event.headers.get(this.authTokenStorageKey));
        }
      }),
      catchError((errors) => {
        if (errors instanceof HttpErrorResponse) {
          if (errors.status === 401 && this.authService.isLoggedIn()) {
            this.authService.logout();
          }
        }
        return throwError(errors);
      })
    );
  }

  private async handleRequest(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Promise<HttpEvent<any>> {
    if (this.authService.isLoggedIn()) {
      request = request.clone({
        setHeaders: {
          Authorization: 'Bearer ' + this.authService.getAuthorizationToken(),
        },
      });
    }
    request = request.clone({ withCredentials: true });
    return next.handle(request).toPromise();
  }

  private saveAccessToken(jwt: string) {
    if (jwt !== undefined) {
      this.autoLogoutService.initializeTokenMonitoring();
      sessionStorage.setItem(this.authTokenStorageKey, jwt);
      this.currentUserService
        .getCurrentUser()
        .subscribe((data) =>
          this.shareDataService.updateUserDetails(data.body)
        );
      return true;
    }
  }
}
