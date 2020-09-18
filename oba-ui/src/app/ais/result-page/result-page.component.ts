import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { AisService } from '../../common/services/ais.service';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss'],
})
export class ResultPageComponent implements OnInit, OnDestroy {
  public authResponse: ConsentAuthorizeResponse;
  public scaStatus: string;
  public ref: string;
  public devPortalLink: string;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private aisService: AisService,
    private settingService: SettingsService,
    private shareService: ShareDataService
  ) {}

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink =
      this.settingService.settings.devPortalUrl +
      '/test-cases/redirect-consent-post';

    // Manual redirect is used because of the CORS error otherwise
    this.route.queryParams.subscribe((params) => {
      let oauth2 = params.oauth2;
      if (oauth2 === undefined || oauth2 !== 'true') {
        oauth2 = false;
      }

      this.ref =
        `/oba-proxy/ais/${params.encryptedConsentId}/authorisation/${params.authorisationId}` +
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
