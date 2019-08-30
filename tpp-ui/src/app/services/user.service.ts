import { Injectable } from '@angular/core';
import {environment} from "../../environments/environment";
import {HttpClient} from "@angular/common/http";
import {User} from "../models/user.model";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  public url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) { }

  listUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.url + '/users');
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
