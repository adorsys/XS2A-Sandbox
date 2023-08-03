/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

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
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': LocalStorageService.get(TPP_REDIRECT_URL_KEY),
    'TPP-Nok-Redirect-URI': LocalStorageService.get(TPP_NOK_REDIRECT_URL_KEY),
  };

  paymentId: string;

  constructor(public localStorageService: LocalStorageService) {
    this.paymentId = LocalStorageService.get('paymentId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
