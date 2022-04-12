/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { map } from 'rxjs/operators';

import { InfoService } from '../../commons/info/info.service';
import { Account } from '../../models/account.model';
import { AccountReport } from '../../models/account-report';
import { UserAccess } from '../../models/user-access';
import { AccountService } from '../../services/account.service';
import { PageNavigationService } from '../../services/page-navigation.service';
import { TppManagementService } from '../../services/tpp-management.service';
import { TppUserService } from '../../services/tpp.user.service';
import { ExtendedBalance } from '../../models/extendedBalance';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss'],
})
export class AccountComponent implements OnInit {
  accountReport: AccountReport;
  balance: ExtendedBalance;
  accountID: string;

  constructor(
    private accountService: AccountService,
    private tppService: TppManagementService,
    private activatedRoute: ActivatedRoute,
    private infoService: InfoService,
    private tppUserService: TppUserService,
    private router: Router,
    private modalService: NgbModal,
    public pageNavigationService: PageNavigationService
  ) {}

  ngOnInit() {
    this.tppUserService.getUserInfo().subscribe(() => {
      this.activatedRoute.params
        .pipe(
          map((response) => {
            return response.id;
          })
        )
        .subscribe((accountID: string) => {
          this.accountID = accountID;
          this.getAccountReport();
        });
    });
  }

  public goToAccountDetail() {
    if (this.isAccountDeleted) {
      this.infoService.openFeedback(
        'You can not Grant Accesses to a Deleted/Blocked account',
        {
          severity: 'error',
        }
      );
    } else {
      this.router.navigate(['/accounts/' + this.account.id + '/access']);
    }
  }

  goToDepositCash() {
    if (!this.isAccountDeleted) {
      this.router.navigate(['/accounts/' + this.account.id + '/deposit-cash']);
    }
  }

  deleteAccountTransactions() {
    this.tppService.deleteAccountTransactions(this.account.id).subscribe(() => {
      this.getAccountReport();
      this.infoService.openFeedback(
        `Transactions of ${this.account.iban} successfully deleted`,
        {
          severity: 'info',
        }
      );
    });
  }

  openDeleteConfirmation(content) {
    this.modalService.open(content).result.then(() => {
      this.deleteAccountTransactions();
    });
  }

  openSetCreditLimitConfirmation(limit) {
    this.modalService.open(limit).result.then(() => {
      console.log(this.account.creditLimit);
      this.setCreditLimit();
    });
  }

  get isAccountDeleted(): boolean {
    if (this.account) {
      return (
        this.account.accountStatus === 'DELETED' ||
        this.account.accountStatus === 'BLOCKED'
      );
    }
    return false;
  }

  get account(): Account {
    return this.accountReport ? this.accountReport.details : null;
  }

  get multilevelScaEnabled(): boolean {
    return this.accountReport.multilevelScaEnabled;
  }

  get accesses(): UserAccess[] {
    return this.accountReport ? this.accountReport.accesses : null;
  }

  getAccountReport() {
    this.accountService
      .getAccountReport(this.accountID)
      .subscribe((report: AccountReport) => {
        this.accountReport = report;
        this.balance = new ExtendedBalance(report.details);
      });
  }

  getValue(data) {
    this.account.creditLimit = data.value;
  }

  private setCreditLimit() {
    this.accountService
      .setCreditLimit(this.account.id, this.account.creditLimit)
      .subscribe(() => this.getAccountReport());
  }
}
