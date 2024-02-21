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

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { AccountDetailsTO } from '../../api/models/account-details-to';
import { ConsentAuthorizeResponse } from '../../api/models/consent-authorize-response';
import { RoutingPath } from '../../common/models/routing-path.model';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-confirm-cancellation',
  templateUrl: './confirm-cancellation.component.html',
  styleUrls: ['./confirm-cancellation.component.scss'],
})
export class ConfirmCancellationComponent implements OnInit, OnDestroy {
  public authResponse: ConsentAuthorizeResponse;
  public encryptedConsentId: string;
  public authorisationId: string;

  private subscriptions: Subscription[] = [];

  constructor(
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private shareService: ShareDataService
  ) {}

  get accounts(): Array<AccountDetailsTO> {
    return this.authResponse ? this.authResponse.accounts : [];
  }

  public ngOnInit(): void {
    this.subscriptions.push(
      this.shareService.currentData.subscribe((data) => {
        if (data) {
          this.shareService.currentData.subscribe((authResponse) => {
            this.authResponse = authResponse;
          });
        }
      })
    );
  }

  public onConfirm() {
    this.router.navigate([
      `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.SELECT_SCA}`,
    ]);
  }

  public onCancel(): void {
    this.router.navigate(
      [`${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
        },
      }
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
