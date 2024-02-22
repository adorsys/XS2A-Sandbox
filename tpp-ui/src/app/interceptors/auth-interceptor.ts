/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { EMPTY, Observable, throwError , from } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ERROR_MESSAGE } from '../commons/constant/constant';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  private errorText = 'Invalid password for user';
  private authTokenStorageKey = 'access_token';

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return from(this.handleRequest(request, next)).pipe(
      tap((event: HttpEvent<any>) => {
        if (event instanceof HttpResponse && event.headers.has(this.authTokenStorageKey)) {
          this.saveAccessToken(event.headers.get(this.authTokenStorageKey));
        }
      }),
      catchError((errors) => {
        if (errors instanceof HttpErrorResponse) {
          if (errors.status === 401 && this.authService.isLoggedIn() && !errors.error?.message.match(this.errorText)) {
            if (errors.status === 401 && errors.statusText?.match('Unauthorized')) {
              errors.error.message = 'You have been logged out due to inactivity.';
              this.authService.logout();
              sessionStorage.setItem(ERROR_MESSAGE, errors.error.message);
              return EMPTY;
            }
            this.authService.logout();
          }
        }
        return throwError(errors);
      })
    );
  }

  private async handleRequest(request: HttpRequest<any>, next: HttpHandler): Promise<HttpEvent<any>> {
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
