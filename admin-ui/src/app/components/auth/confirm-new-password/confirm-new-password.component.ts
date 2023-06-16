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
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';

@Component({
  selector: 'app-confirm-new-password',
  templateUrl: './confirm-new-password.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class ConfirmNewPasswordComponent implements OnInit {
  confirmNewPasswordForm: UntypedFormGroup;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    private authService: AuthService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    public customizeService: CustomizeService
  ) {}

  ngOnInit() {
    this.confirmNewPasswordForm = this.formBuilder.group({
      newPassword: ['', Validators.required],
      code: ['', Validators.required],
    });
  }

  onSubmit() {
    if (this.confirmNewPasswordForm.invalid) {
      this.submitted = true;
      this.errorMessage = 'Please enter your credentials';
      return;
    }

    this.authService
      .changePassword(this.confirmNewPasswordForm.value)
      .subscribe(() => this.router.navigate(['/login']));
  }
}
