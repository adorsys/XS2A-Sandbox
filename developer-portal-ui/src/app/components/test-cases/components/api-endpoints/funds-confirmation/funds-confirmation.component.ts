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
import { JsonService } from '../../../../../services/json.service';

@Component({
  selector: 'app-funds-confirmation',
  templateUrl: './funds-confirmation.component.html',
  styleUrls: ['./funds-confirmation.component.scss'],
})
export class FundsConfirmationComponent implements OnInit {
  activeSegment = 'documentation';
  headers: object = {};
  body;

  constructor(private jsonService: JsonService) {}

  changeSegment(segment) {
    if (segment === 'documentation' || segment === 'play-data') {
      this.activeSegment = segment;
    }
  }

  ngOnInit(): void {
    this.jsonService.getPreparedJsonData(this.jsonService.jsonLinks.fundsConfirmation).subscribe((data) => (this.body = data));
  }
}
