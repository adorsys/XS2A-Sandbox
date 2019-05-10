import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-cancellation-post',
  templateUrl: './emb-payment-cancellation-post.component.html',
  styleUrls: ['./emb-payment-cancellation-post.component.scss'],
})
export class EmbPaymentCancellationPostComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
