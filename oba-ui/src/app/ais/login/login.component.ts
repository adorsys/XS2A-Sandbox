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

import {
  OnlineBankingOauthAuthorizationService,
  PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService,
} from '../../api/services';
import { InfoService } from '../../common/info/info.service';
import { RoutingPath } from '../../common/models/routing-path.model';
import { AisService } from '../../common/services/ais.service';
import { CustomizeService } from '../../common/services/customize.service';
import { ShareDataService } from '../../common/services/share-data.service';
import LoginUsingPOSTParams = PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.LoginUsingPOSTParams;

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: UntypedFormGroup;
  errorMessage: string;

  private encryptedConsentId: string;
  private redirectId: string;
  private subscriptions: Subscription[] = [];

  constructor(
    public customizeService: CustomizeService,
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private infoService: InfoService,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService,
    private onlineBankingOauthAuthorizationService: OnlineBankingOauthAuthorizationService,
    private aisService: AisService
  ) {}

  ngOnInit() {
    this.initLoginForm();

    this.getAisAuthCode();
  }

  public onSubmit(): void {
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter your credentials';
      return;
    }

    this.aisAuthorise({
      pin: this.loginForm.get('pin').value,
      login: this.loginForm.get('login').value,
      encryptedConsentId: this.encryptedConsentId,
      authorisationId: this.redirectId,
    });
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  public aisAuthorise(params: LoginUsingPOSTParams) {
    this.subscriptions.push(
      this.aisService.aisAuthorise(params).subscribe(
        (authorisationResponse) => {
          this.shareService.changeData(authorisationResponse);
          this.router.navigate([
            `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.GRANT_CONSENT}`,
          ]);
        },
        (error: HttpErrorResponse) => {
          console.log('TEST');
          if (
            this.encryptedConsentId === undefined ||
            this.redirectId === undefined
          ) {
            this.infoService.openFeedback(
              'Consent data is missing. Please create consent prior to login',
              {
                severity: 'error',
              }
            );
          } else {
            if (error.status === 401) {
              this.errorMessage = error.error.message;
            } else if (error.status === 400) {
              this.errorMessage =
                'Consent is cancelled and cannot be authorized';
            } else {
              this.errorMessage = error.error
                ? error.error.message
                : error.message;
            }
          }
        }
      )
    );
  }

  public getAisAuthCode(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.encryptedConsentId = params.encryptedConsentId;
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
