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
import { Subject } from 'rxjs';

import { PaymentAuthorizeResponse } from '../../api/models';
import { ShareDataService } from '../../common/services/share-data.service';
import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { takeUntil } from 'rxjs/operators';
import { PsupisprovidesGetPsuAccsService } from '../../api/services/psupisprovides-get-psu-accs.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrls: ['./payment-details.component.scss'],
})
export class PaymentDetailsComponent implements OnInit, OnDestroy {
  private unsubscribe = new Subject();
  dropdownConf: IDropdownSettings = {
    allowSearchFilter: true,
    itemsShowLimit: 10,
    singleSelection: true,
    idField: 'id',
    textField: 'textInDropdown',
  };
  dropdownList = [];
  selectedItems = [];
  isSubmitted = false;
  routerParams = null;

  error = '';

  public authResponse: PaymentAuthorizeResponse;

  constructor(
    private sharedService: ShareDataService,
    private pisAccServices: PsupisprovidesGetPsuAccsService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.pisAccServices
      .getIsSubmitted()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((res) => (this.isSubmitted = res));
    this.route.queryParams
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((route) => {
        this.routerParams = route;
      });
    this.sharedService.currentData.pipe(takeUntil(this.unsubscribe)).subscribe(
      (authResponse: PaymentAuthorizeResponse) => {
        this.authResponse = authResponse;

        if (this.authResponse.payment.debtorAccount) {
          this.pisAccServices.choseIbanAndCurrency = {
            currency: this.authResponse.payment.debtorAccount.currency,
            iban: this.authResponse.payment.debtorAccount.iban,
          };

          if (!this.isSubmitted) {
            this.sendPisInitiate([
              authResponse.payment.debtorAccount.iban,
              authResponse.payment.debtorAccount.currency,
            ]);
          }
        } else if (this.pisAccServices.choseIbanAndCurrency !== null) {
          this.authResponse.payment.debtorAccount = this.pisAccServices.choseIbanAndCurrency;
        } else {
          console.log('both iban and authResponse are null');
        }
      },
      (err) => {
        console.log(err);
      }
    );
    this.setDropdownList();
  }

  ngOnDestroy() {
    this.unsubscribe.next();
    this.unsubscribe.complete();
  }

  setDropdownList(): void {
    this.pisAccServices
      .getAllIban()
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(
        (res) => {
          this.dropdownList = this.addNewField(res);
        },
        (err) => {
          console.error(err);
        }
      );
  }

  get totalAmount(): number {
    if (!this.authResponse || !this.authResponse.payment) {
      return 0;
    }
    let totalAmount = 0;
    this.authResponse.payment.targets.forEach((payment) => {
      totalAmount = totalAmount + payment.instructedAmount.amount;
    });
    return Math.round(totalAmount * 100) / 100;
  }

  addNewField(arr) {
    return arr.map((item) => {
      item.textInDropdown = item.iban + ' ' + item.currency;
      return item;
    });
  }

  sendSelectedIban(text?): void {
    if (!this.selectedItems[0]) {
      this.error = 'Choose Iban';
      return;
    }

    this.error = '';

    const data = this.getDataForInitiate(
      text ? text : this.selectedItems[0].textInDropdown
    );

    this.sendPisInitiate(data);
  }

  getDataForInitiate(val: string): string[] {
    return val.split(' ');
  }

  sendPisInitiate(data) {
    const debtorAccInfo = { currency: data[1], iban: data[0] };
    this.pisAccServices
      .sendPisInitiate(debtorAccInfo, this.routerParams)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe(() => {
        this.pisAccServices.choseIbanAndCurrency = debtorAccInfo;
        this.pisAccServices.setIsSubmitted = true;
      });
  }
}
