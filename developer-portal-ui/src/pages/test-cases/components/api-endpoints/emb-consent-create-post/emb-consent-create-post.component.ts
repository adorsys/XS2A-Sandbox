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
