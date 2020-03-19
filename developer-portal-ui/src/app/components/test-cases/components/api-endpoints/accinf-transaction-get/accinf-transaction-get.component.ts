import { Component } from '@angular/core';

@Component({
  selector: 'app-accinf-transaction-get',
  templateUrl: './accinf-transaction-get.component.html',
})
export class AccinfTransactionGetComponent {
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
