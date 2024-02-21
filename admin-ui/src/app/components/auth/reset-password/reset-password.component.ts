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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../../../services/auth.service';
import { CustomizeService } from '../../../services/customize.service';
import { TppUserService } from '../../../services/tpp.user.service';
import { InfoService } from '@commons/info/info.service';
import { emailValidator } from '@commons/validators/email.validator';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['../auth.component.scss'],
})
export class ResetPasswordComponent implements OnInit, OnDestroy {
  resetPasswordForm: UntypedFormGroup;
  submitted: boolean;
  errorMessage: string;

  private onDestroy = new Subject<void>();

  constructor(
    private authService: AuthService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    public customizeService: CustomizeService,
    private tppUserService: TppUserService,
    private infoService: InfoService
  ) {}

  ngOnInit() {
    this.resetPasswordForm = this.formBuilder.group({
      login: ['', [Validators.required, emailValidator()]],
    });
  }

  ngOnDestroy() {
    this.onDestroy.next();
    this.onDestroy.complete();
  }

  onSubmit() {
    if (this.resetPasswordForm.invalid) {
      this.submitted = true;
      this.errorMessage = 'Please enter valid email';
      return;
    }

    this.tppUserService
      .resetPasswordViaEmail(this.resetPasswordForm.value.login)
      .pipe(takeUntil(this.onDestroy))
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
