import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-consent-put',
  templateUrl: './emb-consent-put.component.html',
  styleUrls: ['./emb-consent-put.component.scss'],
})
export class EmbConsentPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1 = {
    psuData: {
      password: 'YOUR_USER_PASSWORD',
    },
  };
  jsonData2 = {
    scaAuthenticationData: '123456',
  };
  jsonData3 = {
    authenticationMethodId: 'YOUR_AUTHENTICATION_METHOD_ID',
  };
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
