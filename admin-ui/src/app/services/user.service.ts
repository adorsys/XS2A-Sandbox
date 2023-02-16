/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { environment } from '../../environments/environment';
import { PaginationResponse } from '../models/pagination-reponse';
import { User, UserResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  public url = `${environment.tppAdminBackend}`;

  constructor(private http: HttpClient) {}

  listUsers(page = 0, size = 25, queryParam = ''): Observable<UserResponse> {
    return this.http
      .get<PaginationResponse<User[]>>(
        `${this.url}/users?page=${page}&size=${size}&queryParam=${queryParam}`
      )
      .pipe(
        map((resp) => {
          return {
            users: resp.content,
            totalElements: resp.totalElements,
          };
        })
      );
  }

  getUser(userId: string): Observable<User> {
    return this.http.get<User>(this.url + '/users/' + userId);
  }

  createUser(user: User): Observable<any> {
    return this.http.post(this.url + '/users', user);
  }

  updateUserDetails(user: User): Observable<any> {
    return this.http.put(this.url + '/users', user);
  }

  blockTpp(userId: string) {
    return this.http.post(`${this.url}/users/status?userId=${userId}`, userId);
  }

  deleteUser(userId: string) {
    return this.http.delete(`${this.url}/user/${userId}`);
  }

  public resetPasswordViaEmail(login: string): Observable<any> {
    return this.http.post(`${this.url}'/users/reset/password/` + login, null);
  }
}
