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
import { LocalStorageService } from 'src/app/services/local-storage.service';

@Component({
  selector: 'app-emb-payment-cancell-put',
  templateUrl: './emb-payment-cancell-put.component.html',
})
export class EmbPaymentCancellPutComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  headers: object = {
    'PSU-ID': 'YOUR_USER_LOGIN',
  };
  body: object;
  paymentId: string;
  authorisationId: string;

  constructor(private jsonService: JsonService, public localStorageService: LocalStorageService) {
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuData).subscribe(
      (data) => (this.jsonData1 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuData).subscribe(
      (data) => (this.body = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.scaAuthenticationData).subscribe(
      (data) => (this.jsonData2 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.authenticationMethodId).subscribe(
      (data) => (this.jsonData3 = data),
      (error) => console.log(error)
    );

    this.paymentId = LocalStorageService.get('paymentId');
    this.authorisationId = LocalStorageService.get('authorisationId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
