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

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { AisService } from '../../common/services/ais.service';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService } from '../../api/services/psuaisprovides-access-to-online-banking-account-functionality.service';
import { AuthService } from '../../common/services/auth.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss'],
})
export class ResultPageComponent implements OnInit {
  public authResponse: ConsentAuthorizeResponse;
  public scaStatus: string;
  public aisDoneRequest: PSUAISProvidesAccessToOnlineBankingAccountFunctionalityService.AisDoneUsingGETParams;
  public devPortalLink: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private settingService: SettingsService,
    private shareService: ShareDataService,
    private authService: AuthService
  ) {}

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink =
      this.settingService.settings.devPortalUrl +
      '/test-cases/redirect-consent-post';

    // Manual redirect is used because of the CORS error otherwise
    this.route.queryParams.subscribe((params) => {
      let valueOauth2 = params.oauth2;
      if (valueOauth2 === undefined || valueOauth2 !== 'true') {
        valueOauth2 = false;
      }

      this.aisDoneRequest = {
        encryptedConsentId: params.encryptedConsentId,
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
          if (authResponse.authConfirmationCode) {
            this.aisDoneRequest.authConfirmationCode =
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
    this.aisService.aisDone(this.aisDoneRequest).subscribe((resp) => {
      if (resp.redirectUrl) {
        this.authService.clearSession();
        window.location.href = resp.redirectUrl;
      }
    });
  }
}
