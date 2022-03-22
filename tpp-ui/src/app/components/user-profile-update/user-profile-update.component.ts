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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { TppUserService } from '../../services/tpp.user.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { User } from '../../models/user.model';
import { ADMIN_KEY } from '../../commons/constant/constant';
import { InfoService } from '../../commons/info/info.service';
import { Location } from '@angular/common';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-user-profile-update',
  templateUrl: './user-profile-update.component.html',
  styleUrls: ['./user-profile-update.component.scss'],
})
export class UserProfileUpdateComponent implements OnInit, OnDestroy {
  user: User;
  tppId: string;
  userForm: FormGroup;
  submitted: boolean;
  admin: string;
  private currentLogin: string;

  private unsubscribe$ = new Subject<void>();

  constructor(
    private authService: AuthService,
    private userInfoService: TppUserService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private tppManagementService: TppManagementService,
    private infoService: InfoService,
    public location: Location
  ) {}

  ngOnInit() {
    this.admin = sessionStorage.getItem(ADMIN_KEY);
    this.getCurrentUserLogin();
    this.setupEditUserFormControl();
    this.getUserDetails();
    this.setUpTppOrAdmin();
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  get formControl() {
    return this.userForm.controls;
  }

  getUserDetails(): void {
    if (this.authService.isLoggedIn()) {
      this.userInfoService
        .getUserInfo()
        .pipe(takeUntil(this.unsubscribe$))
        .subscribe((user: User) => {
          this.user = user;
          this.admin = user.userRoles.includes('SYSTEM') ? 'true' : 'false';

          this.userForm.patchValue({
            email: this.user.email,
            username: this.user.login,
            //pin: this.user.pin,
          });
        });
    }
  }

  setupEditUserFormControl(): void {
    this.userForm = this.formBuilder.group({
      username: ['', Validators.required],
      email: ['', [Validators.email, Validators.required]],
      //pin: ['', [Validators.minLength(5), Validators.required]],
    });
  }

  onSubmit() {
    if (this.userForm.invalid || this.admin === undefined) {
      return;
    }
    const updatedUser: User = {
      ...this.user,
      branchLogin: undefined,
      email: this.userForm.get('email').value,
      login: this.userForm.get('username').value,
    };

    let restCall;
    if (this.admin === 'true') {
      restCall = this.tppManagementService.updateUserDetails(updatedUser, this.tppId);
    } else {
      restCall = this.userInfoService.updateUserInfo(updatedUser);
    }

    restCall.pipe(takeUntil(this.unsubscribe$)).subscribe(() => {
      this.getUserDetails();
      this.location.back();
      this.infoService.openFeedback('The information has been successfully updated');

      if (this.currentLogin === this.user.login) {
        this.authService.logout();
      }
    });
  }

  private getCurrentUserLogin() {
    this.userInfoService.currentTppUser.pipe(takeUntil(this.unsubscribe$)).subscribe((user) => {
      this.currentLogin = user.login;
    });
  }

  private getUserInfoForAdmin(tppId: string, adminSize?) {
    const restCall = adminSize ? this.tppManagementService.getAdminById(tppId, adminSize) : this.tppManagementService.getTppById(tppId);
    restCall.pipe(takeUntil(this.unsubscribe$)).subscribe((user: User) => {
      if (user) {
        this.user = user;
        this.userForm.patchValue({
          email: this.user.email,
          username: this.user.login,
        });
      }
    });
  }

  private setUpTppOrAdmin() {
    this.tppId = this.route.snapshot.params['id'];
    if (this.admin === 'true') {
      this.route.queryParams.subscribe((params) => {
        this.getUserInfoForAdmin(this.tppId, params['admin']);
      });
    }
  }

  resetPasswordViaEmail(login: string) {
    this.userInfoService.resetPasswordViaEmail(login).subscribe(() => {
      this.infoService.openFeedback('Link for password reset was sent, check email.', {
        severity: 'info',
      });
    });
  }
}
