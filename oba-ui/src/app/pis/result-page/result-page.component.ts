import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { PaymentAuthorizeResponse } from '../../api/models';
import { SettingsService } from '../../common/services/settings.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { PisCancellationService } from './../../common/services/pis-cancellation.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit, OnDestroy {

  public authResponse: PaymentAuthorizeResponse;
  public scaStatus: string;
  public ref: string;
  public devPortalLink: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private pisCancellationService: PisCancellationService,
              private settingService: SettingsService,
              private shareService: ShareDataService) {
  }

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink = this.settingService.settings.devPortalUrl + '/test-cases/redirect-payment-initiation-post';

    // get query params and build link
    this.route.queryParams.subscribe(params => {
      // TODO: use routerlink to build a link https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/8
      this.ref = `/oba-proxy/pis/${params.encryptedConsentId}/authorisation/${params.authorisationId}` +
        `/done?backToTpp=true&forgetConsent=true&oauth2=${params.oauth2}`;
    });

    // get consent data from shared service
    this.shareService.currentData.subscribe(data => {
      if (data) {
        this.shareService.currentData.subscribe(authResponse => {
          this.authResponse = authResponse;
          this.scaStatus = this.authResponse.scaStatus;
        });
      }
    });
  }

  public backToTpp(): void {
    this.pisDone();
  }

  public forgetConsent(): void {
    this.pisDone();
  }

  private pisDone(): void {
    this.pisCancellationService.pisCancellationDone({
      encryptedPaymentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId,
      forgetConsent: 'true',
      backToTpp: 'true'
    }).subscribe(res => console.log(res));
  }

  ngOnDestroy(): void {
  }

}
