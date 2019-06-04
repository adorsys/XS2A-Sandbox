import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-cancellation-post',
  templateUrl: './rdct-payment-cancellation-post.component.html',
  styleUrls: ['./rdct-payment-cancellation-post.component.scss'],
})
export class RdctPaymentCancellationPostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object = {};
  jsonData2: object = {};
  jsonData3: object = {};
  jsonData4: object = {};
  headers: object = {};
  body: object = {};

  constructor() {
    this.init();
  }

  init() {
    this.body = {
      endToEndIdentification: 'D1',
      debtorAccount: {
        currency: 'EUR',
        iban: 'DE80760700240271232400',
      },
      instructedAmount: {
        currency: 'EUR',
        amount: '50.00',
      },
      creditorAccount: {
        currency: 'EUR',
        iban: 'DE15500105172295759744',
      },
      creditorAgent: 'AAAADEBBXXX',
      creditorName: 'WBG',
      creditorAddress: {
        buildingNumber: '56',
        city: 'Nürnberg',
        country: 'DE',
        postalCode: '90543',
        street: 'WBG Straße',
      },
      remittanceInformationUnstructured: 'Ref. Number WBG-1222',
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
    this.jsonData1 = {
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
        city: 'Nürnberg',
        country: 'DE',
        postalCode: '90543',
        street: 'WBG Straße',
      },
      remittanceInformationUnstructured: 'Ref. Number WBG-1222',
    };
    this.jsonData2 = {
      creditorAccount: {
        currency: 'EUR',
        iban: 'DE15500105172295759744',
      },
      creditorAddress: {
        street: 'Breite Gasse',
        buildingNumber: '34',
        city: 'Nürnberg',
        postalCode: '90457',
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
    this.jsonData3 = {
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
            city: 'Nürnberg',
            country: 'DE',
            postalCode: '90543',
            street: 'WBG Straße',
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
            city: 'Dresden',
            country: 'DE',
            postalCode: '01067',
            street: 'Kaisergasse',
          },
          remittanceInformationUnstructured: 'Ref. Number GRUENSTROM-2444',
        },
      ],
    };
    this.jsonData4 = {
      debtorAccount: {
        currency: 'EUR',
        iban: 'YOUR_USER_IBAN',
      },
    };
  }

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
