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
    config: {itemsPerPage, currentPage, totalItems, maxSize} = {
        itemsPerPage: 10,
        currentPage: 1,
        totalItems: 0,
        maxSize: 7
    };
    constructor(private accountService: AccountService,
                public router: Router) {
    }

    ngOnInit() {
        this.getAccounts(this.config.currentPage, this.config.itemsPerPage);
    }

    getAccounts(page: number, size: number): void {
       this.subscription.add(
       this.accountService.getAccounts(page -1, size).subscribe(response => {
           this.accounts = response.accounts;
           this.config.totalItems = response.totalElements;
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

    pageChange(pageNumber: number) {
        this.config.currentPage = pageNumber;
        this.getAccounts(pageNumber, this.config.itemsPerPage);
    }
}
