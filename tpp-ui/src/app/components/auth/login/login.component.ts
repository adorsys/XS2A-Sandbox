/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';
import { ADMIN_KEY, ERROR_MESSAGE } from 'src/app/commons/constant/constant';
import browser from 'browser-detect';
import { MatSnackBar, MatSnackBarHorizontalPosition, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { InfoService } from '../../../commons/info/info.service';
import { MatDialog } from '@angular/material/dialog';
import { ErrorDialogComponent } from '../../../commons/dialog/error-dialog.component';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string;
  horizontalPosition: MatSnackBarHorizontalPosition = 'right';
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  durationInSeconds = 4;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    public customizeService: CustomizeService,
    private _snackBar: MatSnackBar,
    private infoService: InfoService,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    const result = browser();
    if (result.name !== 'chrome' && result.name !== 'edge' && result.name !== 'safari' && result.name !== 'firefox') {
      this._snackBar.open(
        `Unfortunately, you are using an outdated browser. Our website may not look quite right in it. Please consider updating your browser to enjoy an optimal experience.`,
        'Close',
        {
          horizontalPosition: this.horizontalPosition,
          verticalPosition: this.verticalPosition,
          duration: this.durationInSeconds * 1000,
        }
      );
    }
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });

    this.showErrorMessageIfLoggedOutByTimeout();
  }

  onSubmit() {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter your credentials';
      return;
    }

    this.authService.login(this.loginForm.value).subscribe(
      (success) => {
        if (success) {
          this.navigateOnLogin();
        }
      },
      (data) => {
        if (data.status === 401) {
          this.dialog.open(ErrorDialogComponent, {
            height: '200px',
            width: '350px',
            data: { heading: 'Wrong credentials', description: 'You have entered wrong credentials.' },
          });
        }
        if (data.status === 403) {
          this.dialog.open(ErrorDialogComponent, {
            height: '200px',
            width: '350px',
            data: { heading: 'Wrong roles', description: 'You have the wrong role.' },
          });
        }
      }
    );
    this.showErrorMessageIfLoggedOutByTimeout();
  }

  private showErrorMessageIfLoggedOutByTimeout() {
    const message = sessionStorage.getItem(ERROR_MESSAGE);
    if (message != null && message != 'null') {
      this.infoService.openFeedback(sessionStorage.getItem(ERROR_MESSAGE), {
        severity: 'error',
      });
      sessionStorage.setItem(ERROR_MESSAGE, null);
    }
  }

  navigateOnLogin() {
    if (sessionStorage.getItem(ADMIN_KEY) === 'true') {
      this.authService.logout();
      this.infoService.openFeedback('Admin doe not have access to this system', {
        severity: 'error',
      });
    } else {
      this.router.navigate(['/']);
    }
  }
}
