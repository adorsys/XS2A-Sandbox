import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';

import {PaymentAuthorizeResponse} from '../../api/models/payment-authorize-response';
import {SettingsService} from '../../common/services/settings.service';
import {ShareDataService} from '../../common/services/share-data.service';
import {PisService} from '../../common/services/pis.service';

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
  public multilevelSca = false;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private settingService: SettingsService,
              private pisService: PisService,
              private shareService: ShareDataService) {
  }

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink = this.settingService.settings.devPortalUrl + '/test-cases/redirect-payment-initiation-post';

    // Manual redirect is used because of the CORS error otherwise
    this.route.queryParams.subscribe(params => {
      this.ref = `/oba-proxy/pis/${params.encryptedConsentId}/authorisation/${params.authorisationId}` +
        `/done?oauth2=${params.oauth2}`;
    });

    // get consent data from shared service
    this.shareService.currentData.subscribe(data => {
      if (data) {
        this.shareService.currentData.subscribe(authResponse => {
          this.authResponse = authResponse;
          this.scaStatus = this.authResponse.scaStatus;
          const payment = this.authResponse.payment;

          if (payment && payment.transactionStatus == 'PATC') {
            this.multilevelSca = true;
          }

          if (authResponse.authConfirmationCode) {
            this.ref = this.ref + `&authConfirmationCode=${authResponse.authConfirmationCode}`;
          }

        });
      }
    });
  }

  ngOnDestroy(): void {
  }

}
