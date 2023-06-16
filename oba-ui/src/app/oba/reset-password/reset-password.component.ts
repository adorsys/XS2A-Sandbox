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
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../common/services/auth.service';
import { CustomizeService } from '../../common/services/customize.service';
import { InfoService } from '../../common/info/info.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.scss'],
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: UntypedFormGroup;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    public customizeService: CustomizeService,
    private authService: AuthService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private infoService: InfoService
  ) {}

  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      login: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      this.submitted = true;
      this.errorMessage = 'Please enter your credentials';
      return;
    }

    this.authService
      .resetPasswordViaEmail(this.resetPasswordForm.value.login)
      .subscribe(() => {
        this.infoService.openFeedback(
          'Link for password reset was sent, check email.',
          {
            severity: 'info',
          }
        );
        this.router.navigate(['/logout']);
      });
  }
}
