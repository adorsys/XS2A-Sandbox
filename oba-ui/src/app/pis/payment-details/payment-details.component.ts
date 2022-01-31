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

  error = '';

  public authResponse: PaymentAuthorizeResponse;

  constructor(
    private sharedService: ShareDataService,
    private pisAccServices: PsupisprovidesGetPsuAccsService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.sharedService.currentData
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((authResponse) => {
        this.authResponse = authResponse;
        if (this.pisAccServices.choseIbanAndCurrency) {
          this.authResponse.payment.debtorAccount = this.pisAccServices.choseIbanAndCurrency;
        }
      });
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

  sendSelectedIban(): void {
    if (!this.selectedItems[0]) {
      this.error = 'Choose Iban';
      return;
    }

    this.error = '';

    const data = this.getDataForInitiate(this.selectedItems[0].textInDropdown);

    this.sendPisInitiate(data);

    this.isSubmitted = true;
  }

  getDataForInitiate(val: string): string[] {
    return val.split(' ');
  }

  sendPisInitiate(data) {
    const debtorAccInfo = { currency: data[1], iban: data[0] };
    this.pisAccServices
      .sendPisInitiate(debtorAccInfo, this.route.snapshot.queryParams)
      .pipe(takeUntil(this.unsubscribe))
      .subscribe((res) => {
        this.pisAccServices.choseIbanAndCurrency = debtorAccInfo;
      });
  }
}
