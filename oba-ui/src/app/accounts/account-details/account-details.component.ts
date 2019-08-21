import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {OnlineBankingService} from "../../common/services/online-banking.service";
import {AccountDetailsTO, TransactionTO} from "../../api/models";
import {OnlineBankingAccountInformationService} from "../../api/services/online-banking-account-information.service";
import {map} from "rxjs/operators";

@Component({
    selector: 'app-account-details',
    templateUrl: './account-details.component.html',
    styleUrls: ['./account-details.component.scss']
})
export class AccountDetailsComponent implements OnInit {

    account: AccountDetailsTO;
    accountID: string;
    transactions: TransactionTO[];
    transactionsParams: OnlineBankingAccountInformationService.TransactionsUsingGETParams = {
        accountId: '',
        dateFrom: '2019-01-30',
        dateTo: '2019-12-30'
    };

    constructor(private router: Router,
                private activatedRoute: ActivatedRoute,
                private onlineBankingService: OnlineBankingService) {
    }

    ngOnInit() {
        this.activatedRoute.params.pipe(
            map(resp => resp.id)
        ).subscribe((accountID: string) => {
                this.accountID = accountID;
                this.transactionsParams.accountId = accountID;
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
        this.onlineBankingService.getTransactions(this.transactionsParams)
            .subscribe((transactions: TransactionTO[]) => this.transactions = transactions)
    }

}
