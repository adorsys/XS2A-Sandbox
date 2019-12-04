import { Component, OnInit } from '@angular/core';
import { LanguageService } from '../../../../../services/language.service';
import { JsonService } from '../../../../../services/json.service';
import { ConsentTypes } from '../../../../../models/consentTypes.model';
import { AspspService } from '../../../../../services/aspsp.service';

const consentBodies = {
  dedicatedAccountsConsent: {},
  bankOfferedConsent: {},
  globalConsent: {},
  availableAccountsConsent: {},
  availableAccountsConsentWithBalance: {},
};

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
  body: object = { ...consentBodies.dedicatedAccountsConsent };
  consentTypes: ConsentTypes = {
    dedicatedAccountsConsent: consentBodies.dedicatedAccountsConsent,
  };

  constructor(
    public languageService: LanguageService,
    private jsonService: JsonService,
    private aspsp: AspspService
  ) {
    jsonService
      .getJsonData(jsonService.jsonLinks.consent)
      .subscribe(data => (this.jsonData = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.dedicatedAccountsConsent)
      .subscribe(
        data => (consentBodies.dedicatedAccountsConsent = data),
        error => console.log(error)
      );
    jsonService
      .getJsonData(jsonService.jsonLinks.bankOfferedConsent)
      .subscribe(
        data => (consentBodies.bankOfferedConsent = data),
        error => console.log(error)
      );
    jsonService
      .getJsonData(jsonService.jsonLinks.globalConsent)
      .subscribe(
        data => (consentBodies.globalConsent = data),
        error => console.log(error)
      );
    jsonService
      .getJsonData(jsonService.jsonLinks.availableAccountsConsent)
      .subscribe(
        data => (consentBodies.availableAccountsConsent = data),
        error => console.log(error)
      );
    jsonService
      .getJsonData(jsonService.jsonLinks.availableAccountsConsentWithBalance)
      .subscribe(
        data => (consentBodies.availableAccountsConsentWithBalance = data),
        error => console.log(error)
      );
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {
    this.setConsentTypes();
  }

  setConsentTypes() {
    this.aspsp.getAspspProfile().subscribe(object => {
      const allConsentTypes = object.ais.consentTypes;

      if (allConsentTypes.bankOfferedConsentSupported) {
        this.consentTypes.bankOfferedConsent = consentBodies.bankOfferedConsent;
      }
      if (allConsentTypes.globalConsentSupported) {
        this.consentTypes.globalConsent = consentBodies.globalConsent;
      }
      if (allConsentTypes.availableAccountsConsentSupported) {
        this.consentTypes.availableAccountsConsent =
          consentBodies.availableAccountsConsent;
        this.consentTypes.availableAccountsConsentWithBalance =
          consentBodies.availableAccountsConsentWithBalance;
      }
    });
  }
}
