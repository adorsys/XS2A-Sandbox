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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models';
import { RoutingPath } from '../../common/models/routing-path.model';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
import { PsupisprovidesGetPsuAccsService } from '../../api/services/psupisprovides-get-psu-accs.service';
import AuthorisePaymentUsingPOSTParams = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams;

@Component({
  selector: 'app-tan-confirmation',
  templateUrl: './tan-confirmation.component.html',
  styleUrls: ['./tan-confirmation.component.scss'],
})
export class TanConfirmationComponent implements OnInit, OnDestroy {
  public authResponse: PaymentAuthorizeResponse;
  public tanForm: FormGroup;
  public invalidTanCount = 0;

  private subscriptions: Subscription[] = [];
  private operation: string;
  private oauth2Param: boolean;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private pisService: PisService,
    private shareService: ShareDataService,
    private pisAccServices: PsupisprovidesGetPsuAccsService
  ) {}

  public ngOnInit(): void {
    this.initTanForm();

    this.shareService.currentOperation.subscribe((operation: string) => {
      this.operation = operation;
    });

    this.shareService.currentData.subscribe((data) => {
      if (data) {
        console.log('response object: ', data);
        this.shareService.currentData.subscribe(
          (authResponse) => (this.authResponse = authResponse)
        );
      }
    });

    this.shareService.oauthParam.subscribe((oauth2: boolean) => {
      this.oauth2Param = oauth2;
    });
  }

  ngOnDestroy() {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  public onSubmit(): void {
    if (!this.authResponse) {
      return;
    }
    console.log('TAN: ' + this.tanForm.value);

    this.subscriptions.push(
      this.pisService
        .authorizePayment({
          ...this.tanForm.value,
          encryptedPaymentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
        } as AuthorisePaymentUsingPOSTParams)
        .subscribe(
          (authResponse) => {
            console.log(authResponse);
            this.pisAccServices.choseIbanAndCurrency = null;
            this.router
              .navigate(
                [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
                {
                  queryParams: {
                    encryptedConsentId: this.authResponse.encryptedConsentId,
                    authorisationId: this.authResponse.authorisationId,
                    oauth2: this.oauth2Param,
                  },
                }
              )
              .then(() => {
                this.authResponse = authResponse;
                this.shareService.changeData(this.authResponse);
              });
          },
          (error) => {
            this.invalidTanCount++;

            if (this.invalidTanCount >= 3) {
              this.router
                .navigate(
                  [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
                  {
                    queryParams: {
                      encryptedConsentId: this.authResponse.encryptedConsentId,
                      authorisationId: this.authResponse.authorisationId,
                      oauth2: this.oauth2Param,
                    },
                  }
                )
                .then(() => {
                  throw error;
                });
            }
          }
        )
    );
  }

  public onCancel(): void {
    this.router.navigate(
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
        },
      }
    );
  }

  private initTanForm(): void {
    this.tanForm = this.formBuilder.group({
      authCode: ['', Validators.required],
    });
  }
}
