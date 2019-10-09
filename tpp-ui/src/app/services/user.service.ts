import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {User} from "../models/user.model";
import {Observable} from "rxjs";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) { }

  listUsers(page: number = 0, size: number = 25): Observable<{users: User[], totalElements: number}> {
    return this.http.get<User[]>( `${this.url}/users?page=${page}&size=${size}`).pipe(
        map((resp: any) => {
          return {
            users: resp.content,
            totalElements: resp.totalElements
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

}
