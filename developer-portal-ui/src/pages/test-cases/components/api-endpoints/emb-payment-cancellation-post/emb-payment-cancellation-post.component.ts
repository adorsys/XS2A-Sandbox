import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancellation-post',
  templateUrl: './emb-payment-cancellation-post.component.html',
})
export class EmbPaymentCancellationPostComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'TPP-Redirect-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
