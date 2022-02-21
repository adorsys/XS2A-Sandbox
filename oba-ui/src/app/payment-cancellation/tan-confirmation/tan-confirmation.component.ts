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
import { Subject } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { ShareDataService } from '../../common/services/share-data.service';

import AuthorisePaymentUsingPOSTParams = PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService.AuthorisePaymentUsingPOSTParams;
import { PSUPISCancellationProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupiscancellation-provides-access-to-online-banking-payment-functionality.service';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-tan-confirmation',
  templateUrl: './tan-confirmation.component.html',
  styleUrls: ['./tan-confirmation.component.scss'],
})
export class TanConfirmationComponent implements OnInit, OnDestroy {
  public authResponse: PaymentAuthorizeResponse;
  public tanForm: FormGroup;
  public invalidTanCount = 0;

  private unsubscribe = new Subject<void>();
  private operation: string;
  private oauth2Param: boolean;

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private pisCancellationService: PisCancellationService,
    private shareService: ShareDataService
  ) {}

  public ngOnInit(): void {
    this.initTanForm();

    this.shareService.currentOperation
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((operation: string) => {
        this.operation = operation;
      });

    this.shareService.currentData
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((data) => {
        if (data) {
          this.authResponse = data;
          console.log('response object: ', data);
        }
      });

    this.shareService.oauthParam
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((oauth2: boolean) => {
        this.oauth2Param = oauth2;
      });
  }

  ngOnDestroy() {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  public onSubmit(): void {
    // if (!this.authResponse) {
    //   return;
    // }

    this.pisCancellationService
      .authorizePayment({
        ...this.tanForm.value,
        encryptedPaymentId: this.authResponse.encryptedConsentId,
        authorisationId: this.authResponse.authorisationId,
      } as AuthorisePaymentUsingPOSTParams)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(
        (authResponse) => {
          this.router
            .navigate(
              [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
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
                [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
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
      );
  }

  public onCancel(): void {
    this.router.navigate(
      [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
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
