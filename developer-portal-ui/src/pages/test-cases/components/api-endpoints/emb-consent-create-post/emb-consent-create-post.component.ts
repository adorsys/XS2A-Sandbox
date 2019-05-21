import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-consent-create-post',
  templateUrl: './emb-consent-create-post.component.html',
  styleUrls: ['./emb-consent-create-post.component.scss'],
})
export class EmbConsentCreatePostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData = {
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
  headers: object = {};
  body: object = {};

  constructor() {
    this.init();
  }

  init() {
    this.headers = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'TPP-Explicit-Authorisation-Preferred': true,
      'PSU-ID': 'anton.brueckner',
      'PSU-IP-Address': '1.1.1.1',
    };
  }

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
