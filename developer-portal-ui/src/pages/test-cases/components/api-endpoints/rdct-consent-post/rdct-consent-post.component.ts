import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-post',
  templateUrl: './rdct-consent-post.component.html',
  styleUrls: ['./rdct-consent-post.component.scss'],
})
export class RdctConsentPOSTComponent implements OnInit {
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
    combinedServiceIndicator: false,
    frequencyPerDay: 50,
    recurringIndicator: true,
    validUntil: '2019-10-10',
  };
  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
