import {Component, OnDestroy, OnInit} from '@angular/core';
import {Subscription} from "rxjs";
import {OnlineBankingService} from "../common/services/online-banking.service";
import {AccountDetailsTO} from "../api/models/account-details-to";

@Component({
    selector: 'app-accounts',
    templateUrl: './accounts.component.html',
    styleUrls: ['./accounts.component.scss']
})
export class AccountsComponent implements OnInit, OnDestroy {
    accounts: AccountDetailsTO[];
    subscription = new Subscription();

    constructor(private onlineBankingService: OnlineBankingService) {
    }

    ngOnInit() {
        this.getAccounts();
    }

    getAccounts(): void {
        this.subscription.add(
            this.onlineBankingService.getAccounts()
                .subscribe((accounts: AccountDetailsTO[]) => {
                    this.accounts = accounts;
                }));
    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
