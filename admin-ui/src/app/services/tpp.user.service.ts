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
