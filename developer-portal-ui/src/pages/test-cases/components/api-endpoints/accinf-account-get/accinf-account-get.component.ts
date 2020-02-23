import { Component } from '@angular/core';

@Component({
  selector: 'app-accinf-account-get',
  templateUrl: './accinf-account-get.component.html',
})
export class AccinfAccountGetComponent {
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
