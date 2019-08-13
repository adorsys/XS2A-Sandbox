import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-post',
  templateUrl: './rdct-consent-post.component.html',
})
export class RdctConsentPOSTComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData = {
    access: {
      accounts: [],
      balances: [],
      transactions: [],
      availableAccounts: 'allAccounts',
      allPsd2: 'allAccounts',
    },
    combinedServiceIndicator: false,
    frequencyPerDay: 50,
    recurringIndicator: true,
    validUntil: '2020-12-31',
  };
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': 'https://adorsys.de/en/psd2-tpp/',
    'TPP-Nok-Redirect-URI': 'https://www.google.com',
  };
  body: object = {
    access: {
      accounts: [],
      balances: [],
      transactions: [],
      availableAccounts: 'allAccounts',
      allPsd2: 'allAccounts',
    },
    recurringIndicator: true,
    validUntil: '2020-12-31',
    frequencyPerDay: 4,
    combinedServiceIndicator: false,
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
