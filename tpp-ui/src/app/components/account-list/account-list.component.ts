import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";

import {Account} from "../../models/account.model"
import {Subscription} from "rxjs";

@Component({
    selector: 'app-account-list',
    templateUrl: './account-list.component.html',
    styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
    accounts: Account[];
    subscription = new Subscription();

    constructor(private accountService: AccountService) {
    }

    ngOnInit() {
        this.getAccounts();
    }

    getAccounts(): void {
        this.subscription.add(
            this.accountService.getAccounts()
            .subscribe((accounts: Account[]) => {
                this.accounts = accounts;
            }));

    }

    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
