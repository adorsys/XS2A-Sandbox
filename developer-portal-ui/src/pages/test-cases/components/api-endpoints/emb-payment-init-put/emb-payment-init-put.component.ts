import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-init-put',
  templateUrl: './emb-payment-init-put.component.html',
  styleUrls: ['./emb-payment-init-put.component.scss'],
})
export class EmbPaymentInitPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object = {
    psuData: {
      password: 'YOUR_USER_PASSWORD',
    },
  };
  jsonData2: object = {
    scaAuthenticationData: '123456',
  };
  jsonData3: object = {
    authenticationMethodId: 'YOUR_AUTHENTICATION_METHOD_ID',
  };
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };
  body: object = {
    psuData: {
      password: '{{psu_id_password}}',
    },
  };

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
