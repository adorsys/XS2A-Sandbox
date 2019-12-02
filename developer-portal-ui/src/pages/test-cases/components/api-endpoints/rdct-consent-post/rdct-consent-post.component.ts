import { Component, OnInit } from '@angular/core';
import { AspspService } from 'src/services/aspsp.service';
import { ConsentTypes } from '../../../../../models/consentTypes.model';
import { JsonService } from '../../../../../services/json.service';

const consentBodies = {
  dedicatedAccountsConsent: {},
  bankOfferedConsent: {},
  globalConsent: {},
  availableAccountsConsent: {},
  availableAccountsConsentWithBalance: {},
};

@Component({
  selector: 'app-rdct-consent-post',
  templateUrl: './rdct-consent-post.component.html',
})
export class RdctConsentPOSTComponent implements OnInit {
  activeSegment = 'documentation';
  consentTypes: ConsentTypes = {
    dedicatedAccountsConsent: consentBodies.dedicatedAccountsConsent,
  };
  jsonData: object;
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': 'https://adorsys.de/en/psd2-tpp/',
    'TPP-Nok-Redirect-URI': 'https://www.google.com',
  };
  body: object = { ...consentBodies.dedicatedAccountsConsent };

  constructor(private aspsp: AspspService, private jsonService: JsonService) {
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
    jsonService
      .getJsonData(jsonService.jsonLinks.consent)
      .subscribe(data => (this.jsonData = data), error => console.log(error));
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
