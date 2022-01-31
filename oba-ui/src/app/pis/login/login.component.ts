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

import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { CustomizeService } from '../../common/services/customize.service';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { OnlineBankingOauthAuthorizationService } from '../../api/services/online-banking-oauth-authorization.service';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';
import LoginUsingPOST3Params = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.LoginUsingPOST3Params;
import PisAuthUsingGETParams = PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisAuthUsingGETParams;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  errorMessage: string;
  invalidCredentials: boolean;

  private encryptedPaymentId: string;
  private redirectId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: FormBuilder,
    private infoService: InfoService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService,
    private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService,
    private pisService: PisService
  ) {}

  ngOnInit() {
    this.initLoginForm();

    this.getPisAuthCode();
  }

  public onSubmit(): void {
    this.pisAuthorise({
      pin: this.loginForm.get('pin').value,
      login: this.loginForm.get('login').value,
      encryptedPaymentId: this.encryptedPaymentId,
      authorisationId: this.redirectId,
    });
  }

  public pisAuthorise(params: LoginUsingPOST3Params) {
    this.subscriptions.push(
      this.pisService.pisLogin(params).subscribe(
        (authorisationResponse) => {
          console.log(authorisationResponse);
          this.shareService.changePaymentData(authorisationResponse);
          this.router.navigate([
            `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.CONFIRM_PAYMENT}`,
          ]);
        },
        (error: HttpErrorResponse) => {
          // if paymentId or redirectId is missing
          if (
            this.encryptedPaymentId === undefined ||
            this.redirectId === undefined
          ) {
            this.infoService.openFeedback(
              'Payment data is missing. Please initiate payment prior to login',
              {
                severity: 'error',
              }
            );
          } else {
            if (error.status === 401) {
              this.errorMessage = `You don\'t have access to this account.`;
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
      const pisAuthCodeParams: PisAuthUsingGETParams = {
        encryptedPaymentId: this.encryptedPaymentId,
        redirectId: this.redirectId,
        ...(params.token && { Authorization: 'Bearer ' + params.token }),
      };
    });
  }

  private initLoginForm(): void {
    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      pin: ['', Validators.required],
    });
  }
}
