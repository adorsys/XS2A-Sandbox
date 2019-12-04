import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-delete',
  templateUrl: './rdct-payment-cancellation-delete.component.html',
})
export class RdctPaymentCancellationDeleteComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'TPP-Explicit-Authorisation-Preferred': 'true',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'PSU-IP-Address': '1.1.1.1',
    'TPP-REDIRECT-URI': 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
    'TPP-Redirect-Preferred': 'true',
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
