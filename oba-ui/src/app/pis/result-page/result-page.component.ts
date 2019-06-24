import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AisService} from "../../common/services/ais.service";
import {ShareDataService} from "../../common/services/share-data.service";
import {PaymentAuthorizeResponse} from "../../api/models";
import {SettingsService} from "../../common/services/settings.service";

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {

  public authResponse: PaymentAuthorizeResponse;
  public scaStatus: string;
  public ref: string;
  public devPortalLink: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private settingService: SettingsService,
              private shareService: ShareDataService) {
  }

  public ngOnInit(): void {
    // get dev portal link
    this.devPortalLink = this.settingService.settings.devPortalUrl + '/test-cases/redirect-payment-initiation-post';

    // get query params and build link
    this.route.queryParams.subscribe(params => {
      // TODO: use routerlink to build a link https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/8
      this.ref = '/oba-proxy/pis/' + params['encryptedConsentId'] +
        '/authorisation/' + params['authorisationId'] +
        '/done?backToTpp=true&forgetConsent=true';
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

  }

  public forgetConsent(): void {

  }

  ngOnDestroy(): void {
  }

}
