import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancell-delete',
  templateUrl: './emb-payment-cancell-delete.component.html',
})
export class EmbPaymentCancellDeleteComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
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
