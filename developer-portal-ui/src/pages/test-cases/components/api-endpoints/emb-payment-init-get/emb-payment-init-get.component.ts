import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-init-get',
  templateUrl: './emb-payment-init-get.component.html',
  styleUrls: ['./emb-payment-init-get.component.scss'],
})
export class EmbPaymentInitGetComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
