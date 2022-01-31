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
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { PaymentAuthorizeResponse } from '../../api/models';

import { AccountDetailsTO } from '../../api/models/account-details-to';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';

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
  private subscriptions: Subscription[] = [];
  private oauth2Param: boolean;
  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService
  ) {}

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  public ngOnInit(): void {
    this.subscriptions.push(
      this.shareService.currentData.subscribe((data) => {
        if (data) {
          this.shareService.currentData.subscribe((authResponse) => {
            this.authResponse = authResponse;
          });
        }
      })
    );
    this.shareService.currentData.subscribe((data) => {
      if (data) {
        this.shareService.currentData.subscribe((payAuthResponse) => {
          this.payAuthResponse = payAuthResponse;
          this.transactionStatus = this.payAuthResponse.payment.transactionStatus;
        });
      }
    });
    // fetch oauth param value
    this.shareService.oauthParam.subscribe((oauth2: boolean) => {
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
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
