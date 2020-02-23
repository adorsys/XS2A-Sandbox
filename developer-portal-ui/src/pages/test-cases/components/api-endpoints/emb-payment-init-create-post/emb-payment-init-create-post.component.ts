import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-emb-payment-init-create-post',
  templateUrl: './emb-payment-init-create-post.component.html',
})
export class EmbPaymentInitCreatePostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  jsonData4: object;

  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };
  body: object;

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  constructor(private jsonService: JsonService) {
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.singlePayment, true)
      .subscribe(data => (this.jsonData1 = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.periodicPayment)
      .subscribe(data => (this.jsonData2 = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.bulkPayment)
      .subscribe(data => (this.jsonData3 = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(
        jsonService.jsonLinks.singlePaymentPlayWithData,
        true
      )
      .subscribe(data => (this.body = data), error => console.log(error));
    jsonService
      .getPreparedJsonData(jsonService.jsonLinks.debtorAccount)
      .subscribe(data => (this.jsonData4 = data), error => console.log(error));
  }

  ngOnInit() {}
}
