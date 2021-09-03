import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse,
} from '@angular/common/http';
import { Observable, throwError as observableThrowError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  private authTokenStorageKey = 'access_token';

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    return fromPromise(this.handleRequest(request, next)).pipe(
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
        return observableThrowError(errors);
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
    return next.handle(request).toPromise();
  }

  private saveAccessToken(jwt: string) {
    if (jwt !== undefined) {
      sessionStorage.setItem(this.authTokenStorageKey, jwt);
    }
  }
}
