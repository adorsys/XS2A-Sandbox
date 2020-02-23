import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancellation-post',
  templateUrl: './emb-payment-cancellation-post.component.html',
})
export class EmbPaymentCancellationPostComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'TPP-Redirect-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
