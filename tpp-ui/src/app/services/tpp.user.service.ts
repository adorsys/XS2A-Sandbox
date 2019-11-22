import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';

import { environment } from '../../environments/environment';
import { User } from '../models/user.model';
import { tap } from 'rxjs/operators';


@Injectable({
  providedIn: 'root'
})
export class TppUserService {

  private tppUser = new BehaviorSubject<User>(null);
  currentTppUser = this.tppUser.asObservable();
  private url = `${environment.tppBackend}`;

  constructor(private http: HttpClient) { }

  public getUserInfo() {
    return this.http.get<User>(`${this.url}/users/me`).pipe(
      tap(user => this.tppUser.next(user))
    );
  }

  public loadUserInfo(): void {
    this.getUserInfo().subscribe(() => {});
  }

  public updateUserInfo(user: User): Observable<any> {
    return this.http.put(this.url + '/users', user);
  }

}
