import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-emb-payment-init-put',
  templateUrl: './emb-payment-init-put.component.html',
})
export class EmbPaymentInitPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;

  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };
  body: object;

  constructor(private jsonService: JsonService) {
    jsonService
      .getJsonData(jsonService.jsonLinks.psuData)
      .subscribe(data => (this.jsonData1 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.psuData)
      .subscribe(data => (this.body = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.scaAuthenticationData)
      .subscribe(data => (this.jsonData2 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.authenticationMethodId)
      .subscribe(data => (this.jsonData3 = data), error => console.log(error));
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
