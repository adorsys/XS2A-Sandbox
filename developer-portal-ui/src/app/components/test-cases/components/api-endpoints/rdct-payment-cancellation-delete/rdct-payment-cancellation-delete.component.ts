import { Component, OnInit } from '@angular/core';
import { LocalStorageService } from '../../../../../services/local-storage.service';
import { TPP_NOK_REDIRECT_URL_KEY, TPP_REDIRECT_URL_KEY } from '../../../../common/constant/constants';

@Component({
  selector: 'app-rdct-consent-delete',
  templateUrl: './rdct-payment-cancellation-delete.component.html',
})
export class RdctPaymentCancellationDeleteComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': LocalStorageService.get(TPP_REDIRECT_URL_KEY),
    'TPP-Nok-Redirect-URI': LocalStorageService.get(TPP_NOK_REDIRECT_URL_KEY),
  };

  constructor() {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
