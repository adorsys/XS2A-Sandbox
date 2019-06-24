import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private logInFlag = false;
  private login = 'admin';
  private password = 'admin';

  constructor() {}

  logIn(login: string, password: string) {
    if (login === this.login && password === this.password) {
      this.logInFlag = true;
      return true;
    } else {
      this.logInFlag = false;
      return false;
    }
  }

  logOut() {
    this.logInFlag = false;
  }

  isLoggedIn() {
    return this.logInFlag;
  }
}
