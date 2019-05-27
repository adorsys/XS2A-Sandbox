import {HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Observable, throwError as observableThrowError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {fromPromise} from 'rxjs/internal-compatibility';
import {Injectable} from "@angular/core";
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

    constructor(private authService: AuthService) {
    }

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return fromPromise(this.handleRequest(request, next)).pipe(
            catchError(errors => {
                return this.handleErrors(errors);
            })
        );
    }

    private async handleRequest(request: HttpRequest<any>, next: HttpHandler): Promise<HttpEvent<any>> {
        if (this.authService.isLoggedIn()) {
            request = request.clone({setHeaders: {Authorization: 'Bearer ' + this.authService.getAuthorizationToken()}});
        }
        return next.handle(request).toPromise();
    }

    private handleErrors(errorResp: HttpErrorResponse) {
        let error = errorResp.error;

        if (error.status === 403 || error.status === 401) {
            this.authService.logout();
        }
        return observableThrowError(error);
    }
}
