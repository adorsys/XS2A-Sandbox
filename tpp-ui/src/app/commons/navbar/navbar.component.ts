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

import { Component, DoCheck, OnInit } from '@angular/core';

import { AuthService } from '../../services/auth.service';
import { CustomizeService } from '../../services/customize.service';
import { User } from '../../models/user.model';
import { TppUserService } from '../../services/tpp.user.service';

@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss'],
})
export class NavbarComponent implements DoCheck, OnInit {
  public tppUser: User;
  public title: string;
  public openDropdownMenu = false;

  constructor(
    private authService: AuthService,
    public customizeService: CustomizeService,
    private tppUserService: TppUserService
  ) {}

  ngDoCheck(): void {
    if (!this.authService.isLoggedIn()) {
      this.authService.logout();
      throw new Error('Session expired. Please login again.');
    }
    this.title = this.customizeService.getTitle();
  }

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.tppUserService.currentTppUser.subscribe((response: User) => {
        this.tppUser = response;
      });
      this.tppUserService.loadUserInfo();
    }
  }

  onLogout(): void {
    this.authService.logout();
  }
}
