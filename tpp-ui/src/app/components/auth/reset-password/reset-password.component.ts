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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';
import { TppUserService } from '../../../services/tpp.user.service';
import { InfoService } from '../../../commons/info/info.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class ResetPasswordComponent implements OnInit {
  resetPasswordForm: FormGroup;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    public customizeService: CustomizeService,
    private tppUserService: TppUserService,
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
      this.errorMessage = 'Please enter valid login';
      return;
    }

    this.tppUserService.resetPasswordViaEmail(this.resetPasswordForm.value.login).subscribe(() => {
      this.infoService.openFeedback('Link for password reset was sent, check email.', {
        severity: 'info',
      });
      this.router.navigate(['/logout']);
    });
  }
}
