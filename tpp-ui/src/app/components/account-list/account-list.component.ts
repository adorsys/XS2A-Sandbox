import { Component, OnInit } from '@angular/core';
import {AccountService} from "../../services/account.service";

import {Account} from "../../models/account.model"

@Component({
  selector: 'app-account-list',
  templateUrl: './account-list.component.html',
  styleUrls: ['./account-list.component.scss']
})
export class AccountListComponent implements OnInit {
  accounts: Account[];

  constructor(private accountService: AccountService) { }

  ngOnInit() {
    this.getAccounts();
  }


  getAccounts(): void {
    this.accountService.getAccounts()
      .subscribe((accounts: Account[]) => {
        this.accounts = accounts;
      })
  }

}
