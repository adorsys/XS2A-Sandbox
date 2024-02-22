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
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { AccountService } from '../../services/account.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-cash-deposit',
  templateUrl: './cash-deposit.component.html',
  styleUrls: ['./cash-deposit.component.scss'],
})
export class CashDepositComponent implements OnInit {
  cashDepositForm: UntypedFormGroup;

  submitted: boolean;
  accountId: string;
  errorMessage: string;

  constructor(
    private accountService: AccountService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private formBuilder: UntypedFormBuilder
  ) {}

  ngOnInit() {
    this.accountId = this.activatedRoute.snapshot.paramMap.get('id');
    this.cashDepositForm = this.formBuilder.group({
      currency: [{ value: '', disabled: true }, Validators.required],
      amount: ['', [Validators.required, Validators.min(0)]],
    });
    this.accountService
      .getAccount(this.accountId)
      .subscribe((data) =>
        this.cashDepositForm.get('currency').setValue(data['currency'])
      );
  }

  onSubmit() {
    this.submitted = true;
    if (this.cashDepositForm.invalid) {
      return;
    }

    this.accountService
      .depositCash(this.accountId, this.cashDepositForm.getRawValue())
      .subscribe(
        () => this.router.navigate(['/accounts/' + this.accountId]),
        (error) => {
          if (typeof error.error === 'object') {
            this.errorMessage =
              error.error.status +
              ' ' +
              error.error.error +
              ': ' +
              error.error.message;
          } else {
            this.errorMessage = error.status + ' ' + error.error;
          }
        }
      );
  }

  onCancel() {
    this.router.navigate(['/accounts']);
  }
}
