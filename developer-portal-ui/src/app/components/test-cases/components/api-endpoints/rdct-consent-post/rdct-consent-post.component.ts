import { Component, OnInit } from '@angular/core';
import { ConsentTypes } from '../../../../../models/consentTypes.model';
import { JsonService } from '../../../../../services/json.service';
import { SpinnerVisibilityService } from 'ng-http-loader';
import { combineLatest } from 'rxjs';
import { AspspService } from '../../../../../services/aspsp.service';
import { LocalStorageService } from '../../../../../services/local-storage.service';
import { TPP_NOK_REDIRECT_URL_KEY, TPP_REDIRECT_URL_KEY } from '../../../../common/constant/constants';

@Component({
  selector: 'app-rdct-consent-post',
  templateUrl: './rdct-consent-post.component.html',
})
export class RdctConsentPOSTComponent implements OnInit {
  activeSegment = 'documentation';
  consentTypes: ConsentTypes;
  jsonData: object;
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': LocalStorageService.get(TPP_REDIRECT_URL_KEY),
    'TPP-Nok-Redirect-URI': LocalStorageService.get(TPP_NOK_REDIRECT_URL_KEY),
  };
  body;

  constructor(private aspsp: AspspService, private jsonService: JsonService, private spinner: SpinnerVisibilityService) {
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
