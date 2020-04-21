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
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'TPP-Redirect-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
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
    const bodyIndex = 0;
    const jsonDataIndex = 5;

    const observables = combineLatest([
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.dedicatedAccountsConsent),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.bankOfferedConsent),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.globalConsent),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.availableAccountsConsent),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.availableAccountsConsentWithBalance),
      this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.consent),
    ]);

    observables.subscribe((results) => {
      this.body = results[bodyIndex];
      this.jsonData = results[jsonDataIndex];
      this.setConsentTypes(results);
      this.spinner.hide();
    });
  }

  setConsentTypes(results: any[]) {
    const contentTypesIndex = 0;
    const bankOfferedConsentIndex = 1;
    const globalConsentIndex = 2;
    const availableAccountsConsentIndex = 3;
    const availableAccountsConsentWithBalanceIndex = 4;

    this.consentTypes = {
      dedicatedAccountsConsent: results[contentTypesIndex],
    };

    this.aspsp.getAspspProfile().subscribe((object) => {
      const allConsentTypes = object.ais.consentTypes;

      if (allConsentTypes.bankOfferedConsentSupported) {
        this.consentTypes.bankOfferedConsent = results[bankOfferedConsentIndex];
      }
      if (allConsentTypes.globalConsentSupported) {
        this.consentTypes.globalConsent = results[globalConsentIndex];
      }
      if (allConsentTypes.availableAccountsConsentSupported) {
        this.consentTypes.availableAccountsConsent = results[availableAccountsConsentIndex];
        this.consentTypes.availableAccountsConsentWithBalance = results[availableAccountsConsentWithBalanceIndex];
      }
    });
  }
}
