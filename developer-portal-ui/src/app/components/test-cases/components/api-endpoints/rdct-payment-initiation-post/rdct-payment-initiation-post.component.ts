/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { Component, OnInit } from '@angular/core';
import { JsonService } from '../../../../../services/json.service';
import { LocalStorageService } from '../../../../../services/local-storage.service';
import { TPP_NOK_REDIRECT_URL_KEY, TPP_REDIRECT_URL_KEY } from '../../../../common/constant/constants';

@Component({
  selector: 'app-rdct-initiation-post',
  templateUrl: './rdct-payment-initiation-post.component.html',
})
export class RdctPaymentInitiationPostComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  jsonData4: object;

  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
    'TPP-Redirect-Preferred': 'true',
    'TPP-Redirect-URI': LocalStorageService.get(TPP_REDIRECT_URL_KEY),
    'TPP-Nok-Redirect-URI': LocalStorageService.get(TPP_NOK_REDIRECT_URL_KEY),
  };

  body: object;

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  constructor(private jsonService: JsonService) {
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.singlePayment, true).subscribe(
      (data) => (this.jsonData1 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.periodicPayment).subscribe(
      (data) => (this.jsonData2 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.bulkPayment).subscribe(
      (data) => (this.jsonData3 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.debtorAccount).subscribe(
      (data) => (this.jsonData4 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.singlePaymentPlayWithData, true).subscribe(
      (data) => (this.body = data),
      (error) => console.log(error)
    );
  }



  ngOnInit() {}
}
