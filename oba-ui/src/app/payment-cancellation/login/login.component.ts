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

import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { CustomizeService } from '../../common/services/customize.service';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
import LoginUsingPOST2Params = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST2Params;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: UntypedFormGroup;
  errorMessage: string;
  invalidCredentials: boolean;

  public encryptedPaymentId: string;
  public redirectId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: UntypedFormBuilder,
    private infoService: InfoService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private pisCancellationService: PisCancellationService,
    private shareService: ShareDataService
  ) {}

  ngOnInit() {
    this.initLoginForm();

    this.getPisAuthCode();
  }

  public onSubmit(): void {
    this.subscriptions.push(
      this.pisCancellationService
        .pisCancellationLogin({
          ...this.loginForm.value,
          encryptedPaymentId: this.encryptedPaymentId,
          authorisationId: this.redirectId,
        } as LoginUsingPOST2Params)
        .subscribe(
          (authorisationResponse) => {
            console.log(authorisationResponse);
            this.shareService.changePaymentData(authorisationResponse);
            this.router.navigate([
              `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.CONFIRM_CANCELLATION}`,
            ]);
          },
          (error: HttpErrorResponse) => {
            // if paymentId or redirectId is missing
            if (
              this.encryptedPaymentId === undefined ||
              this.redirectId === undefined
            ) {
              this.infoService.openFeedback(
                'Payment data is missing. Please initiate payment cancellation prior to login',
                {
                  severity: 'error',
                }
              );
            } else {
              if (error.status === 401) {
                this.errorMessage = 'Invalid credentials';
              } else {
                this.errorMessage = error.error
                  ? error.error.message
                  : error.message;
              }
              throw new HttpErrorResponse(error);
            }
          }
        )
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  public getPisAuthCode(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.encryptedPaymentId = params.paymentId;
      this.redirectId = params.redirectId;

      // set oauth2 param in shared service
      params.oauth2
        ? this.shareService.setOauthParam(true)
        : this.shareService.setOauthParam(false);
    });
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }
}
