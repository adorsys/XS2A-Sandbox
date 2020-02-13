import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-rdct-consent-delete',
  templateUrl: './rdct-payment-cancellation-delete.component.html',
})
export class RdctPaymentCancellationDeleteComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'TPP-Redirect-URI': 'https://adorsys-platform.de/solutions/xs2a-sandbox/',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Nok-Redirect-URI': 'https://www.google.com',
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
