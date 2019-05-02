import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancell-delete',
  templateUrl: './emb-payment-cancell-delete.component.html',
  styleUrls: ['./emb-payment-cancell-delete.component.scss'],
})
export class EmbPaymentCancellDeleteComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
