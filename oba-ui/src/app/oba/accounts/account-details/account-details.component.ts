import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {debounceTime, map, tap} from 'rxjs/operators';

import {AccountDetailsTO, TransactionTO} from '../../../api/models';
import {OnlineBankingAccountInformationService} from '../../../api/services/online-banking-account-information.service';
import {OnlineBankingService} from '../../../common/services/online-banking.service';
import {CustomNgbDateAdapter, ngbDateToString} from '../../../common/utils/ngb-datepicker-utils';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit {
  account: AccountDetailsTO;
  accountID: string;
  transactions: TransactionTO[];
  filtersGroup: FormGroup;
  formModel: FormGroup;
  config: { itemsPerPage: number, currentPage: number, totalItems: number, maxSize: number } = {
    itemsPerPage: 10,
    currentPage: 1,
    totalItems: 0,
    maxSize: 7
  };

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private fb: FormBuilder,
              private onlineBankingService: OnlineBankingService) {
  }

  ngOnInit() {
    const today = new Date();

    this.filtersGroup = this.fb.group({
      dateFrom: new CustomNgbDateAdapter().fromModel(today),
      dateTo: new CustomNgbDateAdapter().fromModel(today),
    });

    this.formModel = this.fb.group({
      itemsPerPage: [this.config.itemsPerPage, Validators.required]
    });

    this.activatedRoute.params.pipe(
      map(resp => resp.id)
    ).subscribe((accountID: string) => {
        this.accountID = accountID;
        this.getAccountDetail();
        this.refreshTransactions();
      }
    );
    this.onQueryTransactions();
  }

  getAccountDetail() {
    this.onlineBankingService.getAccount(this.accountID)
      .subscribe((account: AccountDetailsTO) => this.account = account);
  }

  refreshTransactions() {
    this.getTransactions(this.config.currentPage, this.config.itemsPerPage);
  }

  getTransactions(page: number, size: number) {
    const params = {
      accountId: this.accountID,
      dateFrom: ngbDateToString(this.filtersGroup.get('dateFrom').value),
      dateTo: ngbDateToString(this.filtersGroup.get('dateTo').value),
      page: page - 1,
      size: size
    } as OnlineBankingAccountInformationService.TransactionsUsingGETParams;
    this.onlineBankingService.getTransactions(params)
      .subscribe(response => {
        this.transactions = response.content;
        this.config.totalItems = response.totalElements;
      });
  }

  pageChange(pageNumber: number) {
    this.config.currentPage = pageNumber;
    this.getTransactions(pageNumber, this.config.itemsPerPage);
  }

  onQueryTransactions() {
    this.formModel.valueChanges.pipe(
      tap(val => {
        this.formModel.patchValue(val, {emitEvent: false});
      }),
      debounceTime(750)
    ).subscribe(form => {
      this.config.itemsPerPage = form.itemsPerPage;
      this.getTransactions(1, this.config.itemsPerPage);
    });
  }
}
