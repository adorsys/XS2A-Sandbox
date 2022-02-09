/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.scss'],
})
export class AccountDetailsComponent implements OnInit, OnDestroy {
  private subscriptions: Subscription[] = [];

  public authResponse: ConsentAuthorizeResponse;

  constructor(private sharedService: ShareDataService) {}

  ngOnInit() {
    this.sharedService.currentData.subscribe(
      (authResponse) => (this.authResponse = authResponse)
    );
  }

  public ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  get accounts(): string[] {
    if (!this.authResponse) {
      return [];
    }
    return this.authResponse.consent.access.accounts.sort() || [];
  }
  get balances(): string[] {
    if (!this.authResponse) {
      return [];
    }
    return this.authResponse.consent.access.balances.sort() || [];
  }
  get transactions(): string[] {
    if (!this.authResponse) {
      return [];
    }
    return this.authResponse.consent.access.transactions.sort() || [];
  }
}
