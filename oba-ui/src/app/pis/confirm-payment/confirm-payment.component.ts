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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subject } from 'rxjs';
import { PaymentAuthorizeResponse } from '../../api/models';

import { AccountDetailsTO } from '../../api/models/account-details-to';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';
import { PsupisprovidesGetPsuAccsService } from '../../api/services/psupisprovides-get-psu-accs.service';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-confirm-payment',
  templateUrl: './confirm-payment.component.html',
  styleUrls: ['./confirm-payment.component.scss'],
})
export class ConfirmPaymentComponent implements OnInit, OnDestroy {
  public payAuthResponse: PaymentAuthorizeResponse;
  public authResponse: ConsentAuthorizeResponse;
  public encryptedConsentId: string;
  public authorisationId: string;
  public transactionStatus: string;
  private ngUnsubscribe = new Subject();
  private oauth2Param: boolean;
  isDisabled = true;

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService,
    private getPsuAccsService: PsupisprovidesGetPsuAccsService
  ) {}

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  public ngOnInit(): void {
    this.getPsuAccsService
      .choseIbanAndCurrencyObservable()
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((res) => {
        this.isDisabled = !!res;
      });

    this.shareService.currentData
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((data) => {
        if (data) {
          console.log('69 string confirm payment');
          this.authResponse = data;
        }
      });

    this.shareService.currentData
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((data) => {
        if (data) {
          console.log('78 string confirm payment');
          this.payAuthResponse = data;
          this.transactionStatus = this.payAuthResponse.payment.transactionStatus;
        }
      });
    // fetch oauth param value
    this.shareService.oauthParam
      .pipe(takeUntil(this.ngUnsubscribe))
      .subscribe((oauth2: boolean) => {
        this.oauth2Param = oauth2;
      });
  }

  public onConfirm() {
    if (this.transactionStatus === 'ACSP') {
      this.router.navigate(
        [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
        {
          queryParams: {
            encryptedConsentId: this.payAuthResponse.encryptedConsentId,
            authorisationId: this.payAuthResponse.authorisationId,
            oauth2: this.oauth2Param,
          },
        }
      );
    } else {
      this.router.navigate([
        `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.SELECT_SCA}`,
      ]);
    }
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

  ngOnDestroy(): void {
    this.ngUnsubscribe.next(true);
    this.ngUnsubscribe.complete();
  }
}
