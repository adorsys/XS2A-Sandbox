import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-rdct-cancellation-post',
  templateUrl: './rdct-payment-cancellation-post.component.html',
})
export class RdctPaymentCancellationPostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  jsonData4: object;
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
    'TPP-Nok-Redirect-URI': 'https://www.google.com',
  };
  body: object;

  constructor(private jsonService: JsonService) {
    jsonService
      .getJsonData(jsonService.jsonLinks.singlePayment)
      .subscribe(data => (this.jsonData1 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.periodicPayment)
      .subscribe(data => (this.jsonData2 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.bulkPayment)
      .subscribe(data => (this.jsonData3 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.debtorAccount)
      .subscribe(data => (this.jsonData4 = data), error => console.log(error));
    jsonService
      .getJsonData(jsonService.jsonLinks.singlePaymentPlayWithData)
      .subscribe(data => (this.body = data), error => console.log(error));
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
