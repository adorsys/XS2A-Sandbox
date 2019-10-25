import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import {debounceTime, map, tap} from 'rxjs/operators';

import { AccountDetailsTO, TransactionTO } from '../../../api/models';
import { OnlineBankingAccountInformationService } from '../../../api/services/online-banking-account-information.service';
import { OnlineBankingService } from '../../../common/services/online-banking.service';
import { ngbDateToString, stringToNgbDate } from '../../../common/utils/ngb-datepicker-utils';

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
    itemPerPageControl: FormGroup;
    config: {itemsPerPage: number, currentPage: number, totalItems: number, maxSize: number} = {
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
        this.filtersGroup = this.fb.group({
          dateFrom: stringToNgbDate('2019-01-30'),
          dateTo: stringToNgbDate('2019-12-30')
        });

        this.itemPerPageControl = this.fb.group({
            query: ['', Validators.required],
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
        this.getTransactions(this.config.currentPage, this.config.itemsPerPage, this.itemPerPageControl.get('query').value);
    }

    getTransactions(page: number, size: number, queryParams: string = '') {
        const params = {
          accountId: this.accountID,
          dateFrom: ngbDateToString(this.filtersGroup.get('dateFrom').value),
          dateTo: ngbDateToString(this.filtersGroup.get('dateTo').value),
          page: page - 1,
          size: size,
          query: queryParams
        } as OnlineBankingAccountInformationService.TransactionsUsingGETParams;
        this.onlineBankingService.getTransactions(params)
            .subscribe(response => {
                this.transactions = response.content;
                this.config.totalItems = response.totalElements;
            });
    }

    pageChange(pageNumber: number) {
        this.config.currentPage = pageNumber;
        this.getTransactions(pageNumber, this.config.itemsPerPage, this.itemPerPageControl.get('query').value);
    }

    onQueryTransactions() {
        this.itemPerPageControl.valueChanges.pipe(
            tap(val => {
                this.itemPerPageControl.patchValue(val, { emitEvent: false });
            }),
            debounceTime(750)
        ).subscribe(form => {
            this.config.itemsPerPage = form.itemsPerPage;
            this.getTransactions(1, this.config.itemsPerPage, form.query);
        });
    }

    public changePageSize(num: number): void {
        this.config.itemsPerPage = this.config.itemsPerPage + num;
    }
}
