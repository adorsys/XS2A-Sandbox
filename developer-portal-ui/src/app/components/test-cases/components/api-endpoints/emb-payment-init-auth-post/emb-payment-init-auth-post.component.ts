import { Component, OnInit } from '@angular/core';
import { LocalStorageService } from '../../../../../services/local-storage.service';

@Component({
  selector: 'app-emb-payment-init-auth-post',
  templateUrl: './emb-payment-init-auth-post.component.html',
})
export class EmbPaymentInitAuthPostComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };

  paymentId: string;

  constructor(public localStorageService: LocalStorageService) {
    this.paymentId = LocalStorageService.get('paymentId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
