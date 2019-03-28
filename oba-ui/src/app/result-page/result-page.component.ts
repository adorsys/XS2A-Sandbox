import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ConsentAuthorizeResponse} from 'api/models';
import {ShareDataService} from '../common/services/share-data.service';
import {AisService} from '../common/services/ais.service';

@Component({
  selector: 'app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {

  public authResponse: ConsentAuthorizeResponse;
  public scaStatus: string;
  public finalised: boolean;
  public failed: boolean;
  public revoked: boolean;
  public ref: string;

  constructor(public router: Router,
              private route: ActivatedRoute,
              private aisService: AisService,
              private shareService: ShareDataService) {
  }

  public ngOnInit(): void {
    this.shareService.currentData.subscribe(data => {
      console.log(data);
      if (data) {
        this.shareService.currentData.subscribe(authResponse => {
          this.authResponse = authResponse;
          this.scaStatus = this.authResponse.scaStatus;

          // TODO: use routerlink to build a link
          this.ref = 'http://localhost:8090/ais/' + this.authResponse.encryptedConsentId +
          '/authorisation/' + this.authResponse.authorisationId +
          '/done?backToTpp=true&forgetConsent=true';

          if (this.scaStatus == 'finalised') {
            this.finalised = true;
          } else {
            this.failed = true;
          }

          console.log(this.scaStatus);
        });
      }
    });
  }

  public backToTpp(): void {
    this.aisDone();
  }

  public forgetConsent(): void {
    this.aisDone();
  }

  private aisDone(): void {
    console.log('done');
    this.aisService.aisDone({
      encryptedConsentId: this.authResponse.encryptedConsentId,
      authorisationId: this.authResponse.authorisationId,
      forgetConsent: 'true',
      backToTpp: 'true'
    }).subscribe(res => console.log(res));
  }
}
