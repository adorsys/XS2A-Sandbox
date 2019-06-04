import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-post',
  templateUrl: './rdct-consent-post.component.html',
  styleUrls: ['./rdct-consent-post.component.scss'],
})
export class RdctConsentPOSTComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData: object = {};
  headers: object = {};
  body: object = {};

  constructor() {
    this.init();
  }

  init() {
    this.jsonData = {
      access: {
        accounts: [
          {
            currency: 'EUR',
            iban: 'YOUR_USER_IBAN',
          },
        ],
        balances: [
          {
            currency: 'EUR',
            iban: 'YOUR_USER_IBAN',
          },
        ],
        transactions: [
          {
            currency: 'EUR',
            iban: 'YOUR_USER_IBAN',
          },
        ],
      },
      combinedServiceIndicator: true,
      frequencyPerDay: 15,
      recurringIndicator: true,
      validUntil: '2019-10-10',
    };
    this.headers = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'TPP-Explicit-Authorisation-Preferred': 'true',
      'PSU-ID': 'YOUR_USER_LOGIN',
      'PSU-IP-Address': '1.1.1.1',
      'TPP-Redirect-Preferred': 'true',
      'TPP-Redirect-URI': 'https://adorsys.de/en/psd2-tpp/',
      'TPP-Nok-Redirect-URI': 'https://www.google.com',
    };
    this.body = {
      access: {
        accounts: [],
        balances: [],
        transactions: [],
        availableAccounts: 'allAccounts',
        allPsd2: 'allAccounts',
      },
      recurringIndicator: false,
      validUntil: '2020-12-31',
      frequencyPerDay: 4,
      combinedServiceIndicator: false,
    };
  }

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
