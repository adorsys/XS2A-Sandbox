import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {catchError, map} from "rxjs/operators";
import {Observable, of} from "rxjs";
import {JwtHelperService} from "@auth0/angular-jwt";
import {Credentials} from "../models/credentials.model";
import {User} from "../models/user.model";
import {Router} from "@angular/router";

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    public url = `${environment.staffAccessResourceEndPoint}`;
    private authTokenStorageKey = 'token';
    private jwtHelperService = new JwtHelperService();
    private headers = new HttpHeaders({
        'Content-Type': 'application/json',
        'accept': 'application/json'
    });

    constructor(private http: HttpClient, private router: Router) {
    }

    authorize(credentials: Credentials): Observable<string> {
        return this.http.post<any>(this.url + '/users/login', credentials)
            .pipe(
                map(loginResponse => loginResponse.bearerToken.access_token)
            );
    }

    login(credentials: any): Observable<boolean> {
        return this.authorize(credentials).pipe(
            map(jwt => {
                localStorage.setItem(this.authTokenStorageKey, jwt);
                return true;
            }),
            catchError(() => of(false))
        );
    }

    isLoggedIn(): boolean {
        return !this.jwtHelperService.isTokenExpired(this.getAuthorizationToken());
    }

    logout() {
        localStorage.removeItem(this.authTokenStorageKey);
        this.router.navigate(['/logout']);
    }

    getAuthorizationToken(): string {
        return localStorage.getItem(this.authTokenStorageKey);
    }

    register(user: User, branch): Observable<any> {
        return this.http.post(this.url + '/users/register', user, {
            params: {branch: branch}
        });
    }

    getTokenExpirationDate(token: string): Date {
        const decoded = this.jwtHelperService.decodeToken(token);
        if (decoded.exp === undefined) return null;
        const date = new Date(0);
        date.setUTCSeconds(decoded.exp);
        return date;
    }
}
