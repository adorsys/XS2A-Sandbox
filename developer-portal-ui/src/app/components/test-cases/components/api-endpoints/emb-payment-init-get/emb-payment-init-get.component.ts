import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-init-get',
  templateUrl: './emb-payment-init-get.component.html',
})
export class EmbPaymentInitGetComponent implements OnInit {
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
