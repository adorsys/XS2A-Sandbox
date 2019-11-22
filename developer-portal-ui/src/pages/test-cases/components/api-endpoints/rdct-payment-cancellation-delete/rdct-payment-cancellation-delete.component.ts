import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-delete',
  templateUrl: './rdct-payment-cancellation-delete.component.html',
})
export class RdctPaymentCancellationDeleteComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object = {
    endToEndIdentification: 'WBG-123456789',
    debtorAccount: {
      currency: 'EUR',
      iban: 'YOUR_USER_IBAN',
    },
    instructedAmount: {
      currency: 'EUR',
      amount: '20.00',
    },
    creditorAccount: {
      currency: 'EUR',
      iban: 'DE15500105172295759744',
    },
    creditorAgent: 'AAAADEBBXXX',
    creditorName: 'WBG',
    creditorAddress: {
      buildingNumber: '56',
      townName: 'Nürnberg',
      country: 'DE',
      postCode: '90543',
      streetName: 'WBG Straße',
    },
    remittanceInformationUnstructured: 'Ref. Number WBG-1222',
  };
  jsonData2: object = {
    creditorAccount: {
      currency: 'EUR',
      iban: 'DE15500105172295759744',
    },
    creditorAddress: {
      streetName: 'Breite Gasse',
      buildingNumber: '34',
      townName: 'Nürnberg',
      postCode: '90457',
      country: 'DE',
    },
    creditorAgent: 'BCENEVOD',
    creditorName: 'Vodafone',
    dayOfExecution: '14',
    debtorAccount: {
      currency: 'EUR',
      iban: 'YOUR_USER_IBAN',
    },
    endDate: '2019-10-14',
    endToEndIdentification: 'VOD-123456789',
    executionRule: 'following',
    frequency: 'Monthly',
    instructedAmount: {
      amount: '44.99',
      currency: 'EUR',
    },
    remittanceInformationUnstructured: 'Ref. Number Vodafone-1222',
    startDate: '2019-05-26',
  };
  jsonData3: object = {
    batchBookingPreferred: 'false',
    requestedExecutionDate: '2019-12-12',
    debtorAccount: {
      currency: 'EUR',
      iban: 'YOUR_USER_IBAN',
    },
    payments: [
      {
        endToEndIdentification: 'WBG-123456789',
        instructedAmount: {
          amount: '520.00',
          currency: 'EUR',
        },
        creditorAccount: {
          currency: 'EUR',
          iban: 'DE15500105172295759744',
        },
        creditorAgent: 'AAAADEBBXXX',
        creditorName: 'WBG',
        creditorAddress: {
          buildingNumber: '56',
          townName: 'Nürnberg',
          country: 'DE',
          postCode: '90543',
          streetName: 'WBG Straße',
        },
        remittanceInformationUnstructured: 'Ref. Number WBG-1234',
      },
      {
        endToEndIdentification: 'RI-234567890',
        instructedAmount: {
          amount: '71.07',
          currency: 'EUR',
        },
        creditorAccount: {
          currency: 'EUR',
          iban: 'DE03500105172351985719',
        },
        creditorAgent: 'AAAADEBBXXX',
        creditorName: 'Grünstrom',
        creditorAddress: {
          buildingNumber: '74',
          townName: 'Dresden',
          country: 'DE',
          postCode: '01067',
          streetName: 'Kaisergasse',
        },
        remittanceInformationUnstructured: 'Ref. Number GRUENSTROM-2444',
      },
    ],
  };
  jsonData4: object = {
    debtorAccount: {
      currency: 'EUR',
      iban: 'YOUR_USER_IBAN',
    },
  };
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
    'TPP-REDIRECT-URI': 'https://adorsys.de/en/psd2-tpp/',
    'TPP-Redirect-Preferred': 'true',
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
