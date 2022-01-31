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
import { Router, ActivatedRoute } from '@angular/router';

import { AuthService } from '../../common/services/auth.service';
import { CustomizeService } from '../../common/services/customize.service';

@Component({
  selector: 'app-confirm-password',
  templateUrl: './confirm-password.component.html',
  styleUrls: ['./confirm-password.component.scss'],
})
export class ConfirmPasswordComponent implements OnInit {
  confirmNewPasswordForm: FormGroup;
  public submitted: boolean;
  public errorMessage: string;

  constructor(
    public customizeService: CustomizeService,
    private authService: AuthService,
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute
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
      .resetPassword(this.confirmNewPasswordForm.value)
      .subscribe(() => this.router.navigate(['/login']));
  }
}
