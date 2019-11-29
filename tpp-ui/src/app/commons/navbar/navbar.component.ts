import { Component, DoCheck, OnInit } from '@angular/core';

import { AuthService } from '../../services/auth.service';
import { CustomizeService } from '../../services/customize.service';
import { User } from '../../models/user.model';
import { TppUserService } from '../../services/tpp.user.service';

@Component({
    selector: 'app-navbar',
    templateUrl: './navbar.component.html',
    styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements DoCheck, OnInit {
  public tppUser: User;
  public openDropdownMenu = false;

  constructor(private authService: AuthService,
              public customizeService: CustomizeService,
              private tppUserService: TppUserService) {}

  ngDoCheck(): void {
      if (!this.authService.isLoggedIn()) {
          this.authService.logout();
          throw new Error('Session expired. Please login again.');
      }
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.tppUserService.currentTppUser.subscribe(
        (response: User) => {
          this.tppUser = response;
        }
      );
      this.tppUserService.loadUserInfo();
    }
  }

  onLogout(): void {
      this.authService.logout();
  }
}
