import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@environment/environment';
import { User } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class TppUserService {
  private url = `${environment.tppAdminBackend}`;

  constructor(private http: HttpClient) {}

  public getUserInfo(): Observable<User> {
    return this.http.get<User>(`${this.url}/users/me`);
  }

  public resetPasswordViaEmail(login: string): Observable<any> {
    return this.http.post(this.url + '/users/reset/password/' + login, null);
  }

  public updateUserInfo(user: User): Observable<any> {
    return this.http.put(this.url + '/users', user);
  }
}
