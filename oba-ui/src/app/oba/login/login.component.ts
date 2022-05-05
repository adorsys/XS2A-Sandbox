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
import { Router } from '@angular/router';
import { Subscription, throwError } from 'rxjs';

import { RoutingPath } from '../../common/models/routing-path.model';
import { AuthService } from '../../common/services/auth.service';
import { CustomizeService } from '../../common/services/customize.service';

import LoginUsingPOST1Params = OnlineBankingAuthorizationProvidesAccessToOnlineBankingService.LoginUsingPOST1Params;
import { OnlineBankingAuthorizationProvidesAccessToOnlineBankingService } from '../../api/services/online-banking-authorization-provides-access-to-online-banking.service';
import {
  MatSnackBarHorizontalPosition,
  MatSnackBarVerticalPosition,
  MatSnackBar,
} from '@angular/material/snack-bar';
import browser from 'browser-detect';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string;
  horizontalPosition: MatSnackBarHorizontalPosition = 'right';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  durationInSeconds = 4;

  private subscriptions: Subscription[] = [];

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    const result = browser();
    if (
      result.name !== 'chrome' &&
      result.name !== 'edge' &&
      result.name !== 'safari' &&
      result.name !== 'firefox'
    ) {
      this.snackBar.open(
        `Unfortunately, you are using an outdated browser. Our website may not look quite right in it. Please consider updating your browser to enjoy an optimal experience.`,
        'Close',
        {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: this.durationInSeconds * 1000,
        }
      );
    }
    this.initLoginForm();

    this.loginForm.valueChanges.subscribe(() => {
      this.errorMessage = null;
    });
  }

  public onSubmit(): void {
    this.subscriptions.push(
      this.authService
        .login({ ...this.loginForm.value } as LoginUsingPOST1Params)
        .subscribe(
          (success) => {
            if (success) {
              this.router.navigate([`${RoutingPath.ACCOUNTS}`]);
            }
          },
          (error) => {
            if (error.status === 401) {
              this.errorMessage = 'Invalid credentials';
            } else {
              this.errorMessage = 'Something went wrong while trying to login';
            }
            return throwError(error);
          }
        )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }
}
