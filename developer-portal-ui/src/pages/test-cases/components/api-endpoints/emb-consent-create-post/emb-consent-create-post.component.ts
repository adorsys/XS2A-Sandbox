import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../../services/language.service';
import { JsonService } from '../../../../../services/json.service';
import { ConsentTypes } from '../../../../../models/consentTypes.model';
import { AspspService } from '../../../../../services/aspsp.service';
import { combineLatest } from 'rxjs';
import { SpinnerVisibilityService } from 'ng-http-loader';

@Component({
  selector: 'app-emb-consent-create-post',
  templateUrl: './emb-consent-create-post.component.html',
})
export class EmbConsentCreatePostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData: object;
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'TPP-Redirect-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };
  body: object;
  consentTypes: ConsentTypes;

  constructor(
    public languageService: LanguageService,
    private jsonService: JsonService,
    private aspsp: AspspService,
    private spinner: SpinnerVisibilityService
  ) {
    this.fetchJsonData();
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}

  fetchJsonData() {
    this.spinner.show();

    const results = combineLatest(
      this.jsonService.getPreparedJsonData(
        this.jsonService.jsonLinks.dedicatedAccountsConsent
      ),
      this.jsonService.getPreparedJsonData(
        this.jsonService.jsonLinks.bankOfferedConsent
      ),
      this.jsonService.getPreparedJsonData(
        this.jsonService.jsonLinks.globalConsent
      ),
      this.jsonService.getPreparedJsonData(
        this.jsonService.jsonLinks.availableAccountsConsent
      ),
      this.jsonService.getPreparedJsonData(
        this.jsonService.jsonLinks.availableAccountsConsentWithBalance
      ),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.consent)
    );

    results.subscribe(results => {
      this.body = results[0];
      this.jsonData = results[5];
      this.setConsentTypes(results);
      this.spinner.hide();
    });
  }

  setConsentTypes(results: any[]) {
    this.consentTypes = {
      dedicatedAccountsConsent: results[0],
    };

    this.aspsp.getAspspProfile().subscribe(object => {
      const allConsentTypes = object.ais.consentTypes;

      if (allConsentTypes.bankOfferedConsentSupported) {
        this.consentTypes.bankOfferedConsent = results[1];
      }
      if (allConsentTypes.globalConsentSupported) {
        this.consentTypes.globalConsent = results[2];
      }
      if (allConsentTypes.availableAccountsConsentSupported) {
        this.consentTypes.availableAccountsConsent = results[3];
        this.consentTypes.availableAccountsConsentWithBalance = results[4];
      }
    });
  }
}
