import { Component } from '@angular/core';

@Component({
  selector: 'app-accinf-balance-get',
  templateUrl: './accinf-balance-get.component.html',
})
export class AccinfBalanceGetComponent {
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
