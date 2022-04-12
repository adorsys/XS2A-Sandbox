/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class TppUserService {
  private tppUser = new BehaviorSubject<User>(null);
  currentTppUser = this.tppUser.asObservable();

  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) {}

  public getUserInfo() {
    return this.http.get<User>(`${this.url}/users/me`).pipe(tap((user) => this.tppUser.next(user)));
  }

  public resetPasswordViaEmail(login: string): Observable<any> {
    return this.http.post(this.url + '/users/reset/password/' + login, null);
  }

  public loadUserInfo(): void {
    this.getUserInfo().subscribe();
  }

  public updateUserInfo(user: User): Observable<any> {
    return this.http.put(this.url + '/users', user);
  }
}
