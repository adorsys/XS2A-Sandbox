import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-payment-status-get',
  templateUrl: './rdct-payment-status-get.component.html',
})
export class RdctPaymentStatusGetComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
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
