import { Injectable } from '@angular/core';

import { environment } from "../../environments/environment";
import { HttpClient } from "@angular/common/http";
import { User } from '../models/user.model';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class TppUserService {

  private user: User;
  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) { }

  public loadUserInfo() {
    return this.http.get(`${this.url}/users/me`);
  }

  public updateUserInfo(user: User): Observable<any> {
    return this.http.put(this.url + '/users', user);
  }

}
