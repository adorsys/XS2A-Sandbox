import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-accinf-accounts-get',
  templateUrl: './accinf-accounts-get.component.html'
})
export class AccinfAccountsGetComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'X-Request-ID': '2f77a125-aa7a-45c0-b414-cea25a116035',
    'Consent-ID': 'CONSENT_ID',
    'PSU-IP-Address': '1.1.1.1',
  };

  constructor() {
  }

  changeSegment(segment) {
    this.activeSegment = segment;
  }

  ngOnInit() {}
}
