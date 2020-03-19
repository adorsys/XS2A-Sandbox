import { Component } from '@angular/core';

@Component({
  selector: 'app-accinf-transactions-get',
  templateUrl: './accinf-transactions-get.component.html',
})
export class AccinfTransactionsGetComponent {
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
