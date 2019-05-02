import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancell-get',
  templateUrl: './emb-payment-cancell-get.html',
  styleUrls: ['./emb-payment-cancell-get.component.scss'],
})
export class EmbPaymentCancellGetComponent implements OnInit {
  activeSegment = 'documentation';
  constructor() {
    this.init();
  }

  init() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
