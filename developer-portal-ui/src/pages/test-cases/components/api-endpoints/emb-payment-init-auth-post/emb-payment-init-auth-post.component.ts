import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-emb-payment-init-auth-post',
  templateUrl: './emb-payment-init-auth-post.component.html',
  styleUrls: ['./emb-payment-init-auth-post.component.scss'],
})
export class EmbPaymentInitAuthPostComponent implements OnInit {
  activeSegment = 'documentation';

  constructor() {}

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
