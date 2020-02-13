import { Component } from '@angular/core';

@Component({
  selector: 'app-accinf-accounts-get',
  templateUrl: './accinf-accounts-get.component.html',
})
export class AccinfAccountsGetComponent {
  activeSegment = 'documentation';
  headers: object = {
    'Consent-ID': 'CONSENT_ID',
  };

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }
}
