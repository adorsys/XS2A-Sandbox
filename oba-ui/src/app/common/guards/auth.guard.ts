import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import {AuthService} from "../services/auth.service";

@Injectable()
export class AuthGuard implements CanActivate {
  constructor(private authService: AuthService) {}

  canActivate() {
    let res = this.authService.isLoggedIn();

    if (!res) {
      this.authService.logout();
      // show login form
    }
    return res;
  }
}
