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

import { Component, OnInit } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../../../models/user.model';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { ScaMethods } from '../../../models/scaMethods';
import { TppUserService } from '../../../services/tpp.user.service';
import { TppManagementService } from '../../../services/tpp-management.service';
import { ADMIN_KEY } from '../../../commons/constant/constant';
import { InfoService } from '../../../commons/info/info.service';
import { ScaUserData } from '../../../models/sca-user-data.model';
import { HttpMethod } from '../../../models/http-method';
import { SettingsService } from '../../../services/settings.service';

@Component({
  selector: 'app-user-update',
  templateUrl: './user-update.component.html',
  styleUrls: ['./user-update.component.scss'],
})
export class UserUpdateComponent implements OnInit {
  public url = `${this.settingsService.settings.tppBackendBasePath}` + '/tpp/push/tan';
  admin: string;
  tppId: string;
  user: User;
  updateUserForm: FormGroup;
  methods: string[];
  httpMethods: string[];

  userId: string;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private tppUserService: TppUserService,
    private tppManagementService: TppManagementService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private infoService: InfoService,
    private settingsService: SettingsService
  ) {
    this.user = new User();
  }

  ngOnInit() {
    this.admin = sessionStorage.getItem(ADMIN_KEY);
    this.setupUserFormControl();
    this.activatedRoute.params
      .pipe(
        map((response) => {
          return response.id;
        })
      )
      .subscribe((id: string) => {
        this.userId = id;
        this.getMethodsValues();
        this.getUserDetails();
      });

    this.tppUserService.currentTppUser.subscribe(() => {
      this.activatedRoute.params.subscribe((param) => {
        this.userId = param['id'];
      });
      this.getUserById();
    });
  }

  getUserById() {
    this.userService.getUser(this.userId).subscribe((user: User) => (this.user = user));
  }

  setupUserFormControl(): void {
    this.updateUserForm = this.formBuilder.group({
      scaUserData: this.formBuilder.array([]),
      email: ['', [Validators.required, Validators.email]],
      login: ['', Validators.required],
    });
  }

  get formControl() {
    return this.updateUserForm.controls;
  }

  onSubmit() {
    this.submitted = true;
    if (this.updateUserForm.invalid) {
      this.errorMessage = 'Please verify your credentials';
      return;
    }
    const updatedUser: User = {
      ...this.user,
      email: this.updateUserForm.get('email').value,
      login: this.updateUserForm.get('login').value,
      scaUserData: this.updateUserForm.get('scaUserData').value,
    };

    this.updateValue(updatedUser);

    if (this.admin === 'true') {
      this.tppManagementService
        .updateUserDetails(updatedUser, this.tppId)
        .subscribe(() => this.router.navigate(['/users/' + `${this.userId}`]));
    } else if (this.admin === 'false') {
      this.userService.updateUserDetails(updatedUser).subscribe(() => this.router.navigate(['/users/' + `${this.userId}`]));
    }
  }

  initScaData() {
    const emailValidators = [
      Validators.required,
      Validators.pattern(
        new RegExp(
          /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
        )
      ),
    ];
    const scaData = this.formBuilder.group({
      id: '',
      scaMethod: [''],
      methodValue: ['', Validators.required],
      staticTan: [{ value: '', disabled: true }],
      usesStaticTan: [false],
      decoupled: [false],
      valid: [false],
      pushMethod: [''],
    });

    scaData.get('usesStaticTan').valueChanges.subscribe((bool = true) => {
      if (bool) {
        scaData.get('staticTan').setValidators([Validators.required, Validators.pattern(new RegExp(/\d{6}/))]);
        scaData.get('staticTan').enable();
      } else {
        scaData.get('staticTan').clearValidators();
        scaData.get('staticTan').disable();
        scaData.get('staticTan').setValue('');
      }
      scaData.get('staticTan').updateValueAndValidity();
    });

    scaData.get('scaMethod').valueChanges.subscribe((value) => {
      if (value === ScaMethods.SMTP_OTP) {
        scaData.get('methodValue').setValidators(emailValidators);
      } else if (value === ScaMethods.MOBILE) {
        scaData
          .get('methodValue')
          .setValidators([Validators.required, Validators.pattern(new RegExp(/^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\s\./0-9]*$/))]);
      } else if (value === ScaMethods.PUSH_OTP) {
        scaData.get('methodValue').clearValidators();
      } else {
        scaData.get('methodValue').setValidators([Validators.required]);
      }
      scaData.get('methodValue').updateValueAndValidity();
    });
    scaData.get('scaMethod').setValue('SMTP_OTP');
    return scaData;
  }

  getUserDetails() {
    this.userService.getUser(this.userId).subscribe((item: User) => {
      this.user = item;
      this.updateUserForm.patchValue({
        email: this.user.email,
        login: this.user.login,
      });
      const scaUserData = <FormArray>this.updateUserForm.get('scaUserData');
      this.user.scaUserData.forEach((value, i) => {
        this.extractScaData(value);
        if (scaUserData.length < i + 1) {
          scaUserData.push(this.initScaData());
        }
        scaUserData.at(i).patchValue(value);
      });
    });
  }

  extractScaData(data: ScaUserData) {
    if (data.scaMethod === 'PUSH_OTP') {
      const strings = data.methodValue.split(',');
      data.pushMethod = strings[0];
      data.methodValue = strings[1];
    }
  }

  updateValue(user: User) {
    user.scaUserData.forEach((d) => {
      if (d.scaMethod === 'PUSH_OTP') {
        if (d.pushMethod === '' || d.pushMethod === undefined) {
          d.pushMethod = 'POST';
        }
        if (d.methodValue === '' || d.methodValue === undefined) {
          d.methodValue = this.url;
        }
        d.methodValue = d.pushMethod + ',' + d.methodValue;
      }
      d.pushMethod = undefined;
    });
  }

  addScaDataItem() {
    const control = <FormArray>this.updateUserForm.controls['scaUserData'];
    control.push(this.initScaData());
  }

  removeScaDataItem(i: number) {
    const control = <FormArray>this.updateUserForm.controls['scaUserData'];
    control.removeAt(i);
  }

  getMethodsValues() {
    this.methods = Object.keys(ScaMethods);
    this.httpMethods = Object.keys(HttpMethod);
  }

  onCancel() {
    if (this.user) {
      this.router.navigate(['/users/' + `${this.userId}`]);
    }
  }

  resetPasswordViaEmail(login: string) {
    this.tppUserService.resetPasswordViaEmail(login).subscribe(() => {
      this.infoService.openFeedback('Link for password reset was sent, check email.', {
        severity: 'info',
      });
    });
  }
}
