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
  selector: 'app-emb-consent-put',
  templateUrl: './emb-consent-put-component-tan.component.html',
})
export class EmbConsentPutTanComponent implements OnInit {
  activeSegment = 'documentation';
  jsonData1: object;
  jsonData2: object;
  jsonData3: object;
  headers: object = {
    'TPP-Explicit-Authorisation-Preferred': 'false',
    'PSU-ID': 'YOUR_USER_LOGIN',
  };
  body: object;
  consentId: string;
  authorisationId: string;

  constructor(private jsonService: JsonService, public localStorageService: LocalStorageService) {
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuDataTan).subscribe(
      (data) => (this.jsonData1 = data),
      (error) => console.log(error)
    );
    this.jsonService.getPreparedJsonData(jsonService.jsonLinks.psuDataTan).subscribe(
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

    this.authorisationId = LocalStorageService.get('authorisationId');
    this.consentId = LocalStorageService.get('consentId');
  }

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit() {}
}
