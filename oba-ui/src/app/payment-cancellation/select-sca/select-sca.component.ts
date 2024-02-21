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

import { Component, OnDestroy, OnInit } from '@angular/core';
import {
  UntypedFormBuilder,
  UntypedFormGroup,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models/payment-authorize-response';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { RoutingPath } from '../../common/models/routing-path.model';
import { PisCancellationService } from '../../common/services/pis-cancellation.service';
import { ShareDataService } from '../../common/services/share-data.service';

@Component({
  selector: 'app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss'],
})
export class SelectScaComponent implements OnInit, OnDestroy {
  public authResponse: PaymentAuthorizeResponse;
  public selectedScaMethod: ScaUserDataTO;
  public scaForm: UntypedFormGroup;

  private subscriptions: Subscription[] = [];

  constructor(
    private formBuilder: UntypedFormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private pisCancellationService: PisCancellationService,
    private shareService: ShareDataService
  ) {
    this.scaForm = this.formBuilder.group({
      scaMethod: ['', Validators.required],
    });
  }

  get scaMehods(): ScaUserDataTO[] {
    return this.authResponse ? this.authResponse.scaMethods : [];
  }

  ngOnInit() {
    this.shareService.currentData.subscribe((data) => {
      if (data) {
        this.shareService.currentData.subscribe((authResponse) => {
          this.authResponse = authResponse;
          if (
            this.authResponse.scaMethods &&
            this.authResponse.scaMethods.length === 1
          ) {
            this.selectedScaMethod = this.authResponse.scaMethods[0];
          }
        });
      }
    });
  }

  public onSubmit(): void {
    if (!this.authResponse || !this.selectedScaMethod) {
      return;
    }

    this.subscriptions.push(
      this.pisCancellationService
        .selectScaMethod({
          encryptedPaymentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
          scaMethodId: this.selectedScaMethod.id,
        })
        .subscribe((authResponse) => {
          this.authResponse = authResponse;
          this.shareService.changeData(this.authResponse);
          this.router.navigate([
            `${RoutingPath.PAYMENT_CANCELLATION}/${RoutingPath.TAN_CONFIRMATION}`,
          ]);
        })
    );
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

  handleMethodSelectedEvent(scaMethod: ScaUserDataTO): void {
    this.selectedScaMethod = scaMethod;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }
}
