import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-payment-status-get',
  templateUrl: './rdct-payment-status-get.component.html',
})
export class RdctPaymentStatusGetComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {};

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
