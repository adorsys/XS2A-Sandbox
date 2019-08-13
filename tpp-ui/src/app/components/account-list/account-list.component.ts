import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from "../../services/account.service";

import {Account} from "../../models/account.model"
import {Subscription} from "rxjs";
import {Router} from "@angular/router";

@Component({
    selector: 'app-account-list',
    templateUrl: './account-list.component.html',
    styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
    accounts: Account[];
    subscription = new Subscription();

    constructor(private accountService: AccountService,
                public router: Router) {
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

    goToDepositCash(account: Account) {
        if (!this.isAccountEnabled(account)) return false;
        this.router.navigate(['/accounts/' + account.id + '/deposit-cash']);
    }

    isAccountEnabled(account: Account): boolean {
        return (account.accountStatus !== "DELETED");
    }


    ngOnDestroy(): void {
        this.subscription.unsubscribe();
    }

}
