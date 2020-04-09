import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';
import { LocalStorageService } from '../../../../../services/local-storage.service';

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
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };
  body;
  paymentId: string;
  authorisationId: string;

  constructor(private jsonService: JsonService, public localStorageService: LocalStorageService) {
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuData).subscribe(
      (data) => (this.jsonData1 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuData).subscribe(
      (data) => (this.body = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.scaAuthenticationData).subscribe(
      (data) => (this.jsonData2 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.authenticationMethodId).subscribe(
      (data) => (this.jsonData3 = data),
      (error) => console.log(error)
    );

    this.paymentId = LocalStorageService.get('paymentId');
    this.authorisationId = LocalStorageService.get('authorisationId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
