import {Component, OnDestroy, OnInit} from '@angular/core';
import {AccountService} from '../../services/account.service';
import {Router} from '@angular/router';
import {Account} from '../../models/account.model';
import {Subscription} from 'rxjs';
import {PageConfig, PaginationConfigModel} from "../../models/pagination-config.model";

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit, OnDestroy {
  accounts: Account[] = [];
  subscription = new Subscription();

  config: PaginationConfigModel = {
    itemsPerPage: 10,
    currentPageNumber: 1,
    totalItems: 0,
  };

  constructor(private accountService: AccountService,
              public router: Router) {}

  ngOnInit() {
    this.getAccounts(this.config.currentPageNumber, this.config.itemsPerPage);
  }

  getAccounts(page: number, size: number) {
    this.accountService.getAccounts(page - 1, size).subscribe(response => {
      this.accounts = response.accounts;
      this.config.totalItems = response.totalElements;
    });
  }

  goToDepositCash(account: Account) {
    if (!this.isAccountEnabled(account)) return false;
    this.router.navigate(['/accounts/' + account.id + '/deposit-cash']);
  }

  isAccountEnabled(account: Account): boolean {
    return (account.accountStatus !== 'DELETED');
  }

  pageChange(pageConfig: PageConfig) {
    this.getAccounts(pageConfig.pageNumber, pageConfig.pageSize);
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
}
