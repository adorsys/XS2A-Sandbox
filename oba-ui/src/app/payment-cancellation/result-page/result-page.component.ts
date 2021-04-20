import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { PisService } from '../../common/services/pis.service';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss'],
})
export class ResultPageComponent implements OnInit, OnDestroy {
  public authResponse: PaymentAuthorizeResponse;
  public scaStatus: string;
  public ref: string;
  public devPortalLink: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private pisService: PisService,
    private settingService: SettingsService,
    private shareService: ShareDataService
  ) {}

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink =
      this.settingService.settings.devPortalUrl +
      '/test-cases/redirect-cancellation-post';

    // Manual redirect is used because of the CORS error otherwise
    this.route.queryParams.subscribe((params) => {
      let oauth2 = params.oauth2;

      if (oauth2 === undefined || typeof oauth2 !== 'boolean') {
        oauth2 = false;
      }

      this.ref =
        `/oba-proxy/pis-cancellation/${params.encryptedConsentId}/authorisation/${params.authorisationId}` +
        `/done?oauth2=${oauth2}`;
    });

    // get consent data from shared service
    this.shareService.currentData.subscribe((data) => {
      if (data) {
        this.shareService.currentData.subscribe((authResponse) => {
          this.authResponse = authResponse;
          this.scaStatus = this.authResponse.scaStatus;
          if (authResponse.authConfirmationCode) {
            this.ref =
              this.ref +
              `&authConfirmationCode=${authResponse.authConfirmationCode}`;
          }
        });
      }
    });
  }

  ngOnDestroy(): void {}
}
