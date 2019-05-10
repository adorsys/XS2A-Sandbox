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
  constructor() {
    this.init();
  }

  init() {
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
