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
import { ActivatedRoute, Router } from '@angular/router';

import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { PisService } from '../../common/services/pis.service';
import { PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService } from '../../api/services/psupisprovides-access-to-online-banking-payment-functionality.service';
import { AuthService } from '../../common/services/auth.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss'],
})
export class ResultPageComponent implements OnInit {
  public authResponse: PaymentAuthorizeResponse;
  public scaStatus: string;
  public pisDoneRequest: PSUPISProvidesAccessToOnlineBankingPaymentFunctionalityService.PisDoneUsingGET1Params;
  public devPortalLink: string;
  public multilevelSca = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private settingService: SettingsService,
    private pisService: PisService,
    private shareService: ShareDataService,
    private authService: AuthService
  ) {}

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink =
      this.settingService.settings.devPortalUrl +
      '/test-cases/redirect-payment-initiation-post';

    // Manual redirect is used because of the CORS error otherwise
    this.route.queryParams.subscribe((params) => {
      let valueOauth2 = params.oauth2;

      if (valueOauth2 === undefined || valueOauth2 !== 'true') {
        valueOauth2 = false;
      }

      this.pisDoneRequest = {
        encryptedPaymentId: params.encryptedConsentId,
        authorisationId: params.authorisationId,
        oauth2: valueOauth2,
        authConfirmationCode: null,
      };
    });

    // get consent data from shared service
    this.shareService.currentData.subscribe((data) => {
      if (data) {
        this.shareService.currentData.subscribe((authResponse) => {
          this.authResponse = authResponse;
          this.scaStatus = this.authResponse.scaStatus;
          const payment = this.authResponse.payment;

          if (payment && payment.transactionStatus === 'PATC') {
            this.multilevelSca = true;
          }

          if (authResponse.authConfirmationCode) {
            this.pisDoneRequest.encryptedPaymentId =
              authResponse.authConfirmationCode;
          }
        });
      }
    });
  }

  redirectToDevPortal() {
    if (this.devPortalLink) {
      this.authService.clearSession();
      window.location.href = this.devPortalLink;
    }
  }

  redirectToTpp() {
    this.pisService.pisDone(this.pisDoneRequest).subscribe((resp) => {
      if (resp.redirectUrl) {
        this.authService.clearSession();
        window.location.href = resp.redirectUrl;
      }
    });
  }
}
