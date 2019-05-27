import {Component, OnDestroy, OnInit} from '@angular/core';
import {PaymentAuthorizeResponse} from "../../api/models/payment-authorize-response";
import {ActivatedRoute, Router} from "@angular/router";
import {AisService} from "../../common/services/ais.service";
import {ShareDataService} from "../../common/services/share-data.service";

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit, OnDestroy {

  public authResponse: PaymentAuthorizeResponse;
  public scaStatus: string;
  public ref: string;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private shareService: ShareDataService) {
  }

  public ngOnInit(): void {
    // get query params and build link
    this.route.queryParams.subscribe(params => {
      // TODO: use routerlink to build a link https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/8
      this.ref = '/oba-proxy/pis-cancellation/' + params['encryptedConsentId'] +
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
