import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancell-put',
  templateUrl: './emb-payment-cancell-put.component.html',
  styleUrls: ['./emb-payment-cancell-put.component.scss'],
})
export class EmbPaymentCancellPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object = {};
  jsonData2: object = {};
  jsonData3: object = {};
  headers: object = {};
  body: object = {};

  constructor() {
    this.init();
  }

  init() {
    this.headers = {
      'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
      'TPP-Explicit-Authorisation-Preferred': 'true',
      'PSU-ID': 'YOUR_USER_LOGIN',
      'PSU-IP-Address': '1.1.1.1',
    };
    this.jsonData1 = {
      psuData: {
        password: 'YOUR_USER_PASSWORD',
      },
    };
    this.jsonData2 = {
      scaAuthenticationData: '123456',
    };
    this.jsonData3 = {
      authenticationMethodId: 'YOUR_AUTHENTICATION_METHOD_ID',
    };
  }

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
