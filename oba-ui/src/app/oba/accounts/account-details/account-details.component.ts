import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs/operators';

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

        this.activatedRoute.params.pipe(
            map(resp => resp.id)
        ).subscribe((accountID: string) => {
                this.accountID = accountID;
                this.getAccountDetail();
                this.getTransactions();
            }
        )
    }

    getAccountDetail() {
        this.onlineBankingService.getAccount(this.accountID)
            .subscribe((account: AccountDetailsTO) => this.account = account);
    }

    getTransactions() {
        const params = {
          accountId: this.accountID,
          dateFrom: ngbDateToString(this.filtersGroup.get('dateFrom').value),
          dateTo: ngbDateToString(this.filtersGroup.get('dateTo').value)
        } as OnlineBankingAccountInformationService.TransactionsUsingGETParams;
        this.onlineBankingService.getTransactions(params)
            .subscribe((transactions: TransactionTO[]) => this.transactions = transactions);
    }

}
