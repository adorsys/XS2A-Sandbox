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

import { Component, OnInit } from '@angular/core';
import { UserTO } from '../../api/models/user-to';
import { CurrentUserService } from '../../common/services/current-user.service';

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrls: ['./user-profile.component.scss'],
})
export class UserProfileComponent implements OnInit {
  public obaUser: UserTO;

  constructor(private currentUserService: CurrentUserService) {}

  ngOnInit() {
    this.getUserInfo();
  }

  public getUserInfo() {
    this.currentUserService.getCurrentUser().subscribe((data) => {
      if (data.body !== undefined && data.body !== null) {
        return (this.obaUser = data.body);
      }
    });
  }
}
