import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-consent-put',
  templateUrl: './emb-consent-put.component.html',
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
  body: object = {
    psuData: {
      password: '{{psu_id_password}}',
    },
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
