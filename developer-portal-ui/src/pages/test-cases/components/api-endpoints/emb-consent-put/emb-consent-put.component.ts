import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-emb-consent-put',
  templateUrl: './emb-consent-put.component.html',
})
export class EmbConsentPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };
  body: object;

  constructor(private jsonService: JsonService) {
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.psuData)
      .subscribe(data => (this.jsonData1 = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.psuData)
      .subscribe(data => (this.body = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.scaAuthenticationData)
      .subscribe(data => (this.jsonData2 = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.authenticationMethodId)
      .subscribe(data => (this.jsonData3 = data), error => console.log(error));
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
