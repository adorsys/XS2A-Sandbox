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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models';
import { ScaUserDataTO } from '../../api/models/sca-user-data-to';
import { RoutingPath } from '../../common/models/routing-path.model';
import { PisService } from '../../common/services/pis.service';
import { ShareDataService } from '../../common/services/share-data.service';
import { SettingsService } from 'src/app/common/services/settings.service';

@Component({
  selector: 'app-select-sca',
  templateUrl: './select-sca.component.html',
  styleUrls: ['./select-sca.component.scss'],
})
export class SelectScaComponent implements OnInit, OnDestroy {
  public authResponse: PaymentAuthorizeResponse;
  public selectedScaMethod: ScaUserDataTO;
  public scaForm: FormGroup;
  public isScaMethodNotAvailable = false;
  public devPortalLink: string;

  public subscriptions: Subscription[] = [];

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private pisService: PisService,
    private shareService: ShareDataService,
    private settingService: SettingsService
  ) {
    this.scaForm = this.formBuilder.group({
      scaMethod: ['', Validators.required],
    });
  }

  get scaMethods(): ScaUserDataTO[] {
    return this.authResponse ? this.authResponse.scaMethods : [];
  }

  ngOnInit() {
    this.devPortalLink =
      this.settingService.settings.devPortalUrl +
      '/test-cases/redirect-payment-initiation-post';

    this.shareService.currentData.subscribe((data) => {
      if (data) {
        // TODO extract the Accounts, Balances and Transactions from data.value
        //  https://git.adorsys.de/adorsys/xs2a/psd2-dynamic-sandbox/issues/9
        this.shareService.currentData.subscribe((authResponse) => {
          this.authResponse = authResponse;
          if (this.authResponse.scaMethods) {
            this.isScaMethodNotAvailable = false;
            this.selectedScaMethod = this.authResponse.scaMethods[0];
            console.log(this.selectedScaMethod);
            this.scaForm
              .get('scaMethod')
              .setValue(this.selectedScaMethod.id, { emitEvent: false });
          } else {
            this.isScaMethodNotAvailable = true;
          }
        });
      }
    });

    this.scaForm.get('scaMethod').valueChanges.subscribe((id) => {
      const selectedScaMethod = this.scaMethods.find(
        (scaMethod) => scaMethod.id === id
      );
      this.handleMethodSelectedEvent(selectedScaMethod);
    });
  }

  public onSubmit(): void {
    if (!this.authResponse || !this.selectedScaMethod) {
      return;
    }

    this.subscriptions.push(
      this.pisService
        .selectScaMethod({
          encryptedPaymentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
          scaMethodId: this.selectedScaMethod.id,
        })
        .subscribe((authResponse) => {
          this.authResponse = authResponse;
          this.shareService.changeData(this.authResponse);
          if (this.selectedScaMethod.decoupled) {
            this.router.navigate([
              `${RoutingPath.ACCOUNT_INFORMATION}/${RoutingPath.RESULT}`,
            ]);
          } else {
            this.router.navigate([
              `${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.TAN_CONFIRMATION}`,
            ]);
          }
        })
    );
  }

  public onCancel(): void {
    this.router.navigate(
      [`${RoutingPath.PAYMENT_INITIATION}/${RoutingPath.RESULT}`],
      {
        queryParams: {
          encryptedConsentId: this.authResponse.encryptedConsentId,
          authorisationId: this.authResponse.authorisationId,
        },
      }
    );
  }

  handleMethodSelectedEvent(scaMethod: ScaUserDataTO): void {
    if (!scaMethod) {
      console.log('No sca method selected.');
    }
    this.selectedScaMethod = scaMethod;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  public isScaSelected() {
    return !this.selectedScaMethod;
  }
}
