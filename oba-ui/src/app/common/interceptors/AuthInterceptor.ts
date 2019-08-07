import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';
import {Observable, from, throwError} from 'rxjs';
import {Injectable} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {catchError} from 'rxjs/operators';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return from(this.handleRequest(request, next)).pipe(
      catchError(errors => {
        return throwError(errors);
      })
    );
  }

  private async handleRequest(request: HttpRequest<any>, next: HttpHandler): Promise<HttpEvent<any>> {
    if (this.authService.isLoggedIn()) {
      request = request.clone({setHeaders: {Authorization: 'Bearer ' + this.authService.getAuthorizationToken()}});
    }
    request = request.clone({withCredentials: true});
    return next.handle(request).toPromise();
  }
}
