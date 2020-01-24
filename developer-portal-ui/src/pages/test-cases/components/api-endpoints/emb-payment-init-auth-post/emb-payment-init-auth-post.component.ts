import { Component, OnInit } from '@angular/core';
import { LocalStorageService } from '../../../../../services/local-storage.service';

@Component({
  selector: 'app-emb-payment-init-auth-post',
  templateUrl: './emb-payment-init-auth-post.component.html',
})
export class EmbPaymentInitAuthPostComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
  };

  paymentId: string;

  constructor(public localStorageService: LocalStorageService) {
    this.paymentId = this.localStorageService.get('paymentId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
