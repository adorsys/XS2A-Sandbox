import {Component, OnInit} from '@angular/core';
import {ConsentTypes} from '../../../../../models/consentTypes.model';
import {JsonService} from '../../../../../services/json.service';
import {SpinnerVisibilityService} from 'ng-http-loader';
import {combineLatest} from 'rxjs';
import {AspspService} from "../../../../../services/aspsp.service";

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
    'TPP-Redirect-URI': 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
    'TPP-Nok-Redirect-URI': 'https://www.google.com',
  };
  body;

  constructor(
    private aspsp: AspspService,
    private jsonService: JsonService,
    private spinner: SpinnerVisibilityService
  ) {
    this.fetchJsonData();
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {
  }

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
